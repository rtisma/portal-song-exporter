package org.icgc.dcc.song.tools.exporter;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.icgc.dcc.song.client.register.Registry;
import org.icgc.dcc.song.client.register.RestClient;
import org.icgc.dcc.song.tools.exporter.download.fetcher.DataFetcher;
import org.icgc.dcc.song.tools.exporter.model.PortalFileMetadata;

import java.nio.file.Path;

import static org.icgc.dcc.song.tools.exporter.convert.PortalUrlConverter.createPortalUrlConverter;
import static org.icgc.dcc.song.tools.exporter.download.DownloadIterator.createDownloadIterator;
import static org.icgc.dcc.song.tools.exporter.download.PortalDonorIdFetcher.createPortalDonorIdFetcher;
import static org.icgc.dcc.song.tools.exporter.download.fetcher.DataFetcher.createDataFetcher;
import static org.icgc.dcc.song.tools.exporter.download.fetcher.DonorFetcher.createDonorFetcher;
import static org.icgc.dcc.song.tools.exporter.download.urlgenerator.FilePortalUrlGenerator.createFilePortalUrlGenerator;
import static org.icgc.dcc.song.tools.exporter.filters.BypassFilter.createBypassFilter;

@Builder
@RequiredArgsConstructor
public class Factory {

  @NonNull private final String songServerUrl;
  @NonNull private final Path outputDirpath;
  private final int numThreads;
  private final int batchSize;
  @NonNull private final String portalApiUrl;
  @NonNull private final String portalRepoName;
  private final int portalFetchSize;
  @NonNull private final String jqlQuery;

  public PortalExporter buildPortalExtractor(){
    val dataFetcher = buildDataFetcherForJQL();
    val songClient = buildSongClient(songServerUrl);
    return PortalExporter.builder()
        .dataFetcher(dataFetcher)
        .numThreads(numThreads)
        .outputDir(outputDirpath)
        .partitionSize(batchSize)
        .songClient(songClient)
        .build();
  }

  private DataFetcher buildDataFetcherForJQL(){
    val portalDonorIdFetcher = createPortalDonorIdFetcher(portalApiUrl);
    val donorFetcher = createDonorFetcher(portalDonorIdFetcher);
    val portalUrlConverter = createPortalUrlConverter(portalRepoName);
    val filePortalUrlGenerator = createFilePortalUrlGenerator(portalApiUrl, portalRepoName, jqlQuery);
    val downloadIterator = createDownloadIterator(portalUrlConverter, filePortalUrlGenerator, portalFetchSize, portalFetchSize, 1);
    return createDataFetcher(donorFetcher, createBypassFilter(PortalFileMetadata.class), downloadIterator );
  }

  private static Registry buildSongClient(String songServerUrl){
    // Setup song
    val songConfig = new org.icgc.dcc.song.client.config.Config();
    songConfig.setServerUrl(songServerUrl);
    songConfig.setStudyId("ABC123");
    val restClient = new RestClient();
    return new Registry(songConfig, restClient);
  }

}
