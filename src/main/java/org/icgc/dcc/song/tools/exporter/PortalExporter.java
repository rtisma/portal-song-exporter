package org.icgc.dcc.song.tools.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc.dcc.song.client.register.Registry;
import org.icgc.dcc.song.core.model.ExportedPayload;
import org.icgc.dcc.song.tools.exporter.download.fetcher.DataFetcher;
import org.icgc.dcc.song.tools.exporter.model.PortalFileMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Iterables.partition;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toMap;
import static lombok.Lombok.sneakyThrow;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableSet;
import static org.icgc.dcc.common.core.util.stream.Streams.stream;
import static org.icgc.dcc.song.core.utils.JsonUtils.fromJson;
import static org.icgc.dcc.song.core.utils.JsonUtils.toPrettyJson;

@Slf4j
@Builder
@RequiredArgsConstructor
public class PortalExporter implements Runnable {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String ANALYSIS_ID = "analysisId";

  /**
   * Dependencies
   */
  @NonNull private final DataFetcher dataFetcher;
  @NonNull private final Registry songClient;

  /**
   * Config
   */
  @NonNull private final Path outputDir;
  @NonNull private final int numThreads;
  @NonNull private final int partitionSize;

  /**
   * State
   */
  private final AtomicInteger fileCount = new AtomicInteger(0);

  @SneakyThrows
  public void run(){
    val portalFiles = dataFetcher.fetchPortalFileMetadata();
    val studyIds = portalFiles.stream().map(PortalFileMetadata::getProjectCode).collect(toImmutableSet());
    val analysisIds = portalFiles.stream().map(PortalFileMetadata::getRepoDataBundleId).collect(toImmutableSet());
    createStudyDirectories(outputDir, studyIds);

    val partitions = partition(newArrayList(analysisIds), partitionSize);
    val executorService = newFixedThreadPool(numThreads);

    int batchCount = 0;
    val size = size(partitions);
    for (val partitionOfAnalysisIds : partitions){
      executorService.submit(() -> exportAndSave(partitionOfAnalysisIds));
      log.info("Submitted Batch {} / {}", ++batchCount, size);
    }
    executorService.shutdown();
    executorService.awaitTermination(5, TimeUnit.HOURS);
  }

  private void exportAndSave(List<String> analysisIds){
    val exportStatus = songClient.exportAnalyses(analysisIds, true);
    val json = exportStatus.getOutputs();
    jsonToDisk(json);
  }

  @SneakyThrows
  private void jsonToDisk(String json){
    val root = OBJECT_MAPPER.readTree(json);
    stream(root.iterator())
        .map(j -> fromJson(j, ExportedPayload.class))
        .forEach(x -> exportedPayloadToFile(x, outputDir));
  }

  @SneakyThrows
  private void exportedPayloadToFile(ExportedPayload exportedPayload, Path dirPath){
    val studyDir = dirPath.resolve(exportedPayload.getStudyId());
    if(!Files.exists(studyDir)){
      createDirectories(studyDir);
    }
    for (val jsonNode : exportedPayload.getPayloads()){
      String fileName;
      if(jsonNode.has(ANALYSIS_ID)){
        fileName = format("%s.json", jsonNode.path(ANALYSIS_ID).textValue());
      } else {
        fileName = format("payload_%s.json", fileCount.getAndIncrement());
      }
      val filePath = studyDir.resolve(fileName);
      val bw = newBufferedWriter(filePath);
      bw.write(toPrettyJson(jsonNode));
      bw.close();
    }
  }

  private static Map<String, Path> createStudyDirectories(Path outputDir, Set<String> studyIds){
    return studyIds.stream()
        .peek(x -> {
          Path p = outputDir.resolve(x);
          if (!Files.exists(p)){
            try {
              createDirectories(p);
            } catch (IOException e) {
              throw sneakyThrow(e);
            }
          }
        })
        .collect(toMap(x -> x, outputDir::resolve));
  }

}
