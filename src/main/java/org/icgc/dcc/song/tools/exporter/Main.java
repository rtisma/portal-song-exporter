package org.icgc.dcc.song.tools.exporter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.file.Files.readAllBytes;
import static org.icgc.dcc.song.tools.exporter.Config.BATCH_SIZE;
import static org.icgc.dcc.song.tools.exporter.Config.NUM_THREADS;
import static org.icgc.dcc.song.tools.exporter.Config.OUTPUT_DIRPATH;
import static org.icgc.dcc.song.tools.exporter.Config.PORTAL_API_URL;
import static org.icgc.dcc.song.tools.exporter.Config.PORTAL_FETCH_SIZE;
import static org.icgc.dcc.song.tools.exporter.Config.PORTAL_REPO_NAME;
import static org.icgc.dcc.song.tools.exporter.Config.SONG_COLLAB_URL;

@Slf4j
public class Main {

  @SneakyThrows
  public static void main(String[] args){
    val inputJQLFile = getFileFromArgument(args);
    val jqlQuery = readFile(inputJQLFile);
    val factory = Factory.builder()
        .batchSize(BATCH_SIZE)
        .numThreads(NUM_THREADS)
        .outputDirpath(OUTPUT_DIRPATH)
        .portalApiUrl(PORTAL_API_URL)
        .portalFetchSize(PORTAL_FETCH_SIZE)
        .portalRepoName(PORTAL_REPO_NAME)
        .songServerUrl(SONG_COLLAB_URL)
        .jqlQuery(jqlQuery)
        .build();
    val portalExtractor = factory.buildPortalExtractor();
    portalExtractor.run();
  }

  private static Path getFileFromArgument(String[] args){
    val filename = args[0];
    val path = Paths.get(filename);
    checkArgument(Files.exists(path), "The input filename '%s' does not exist",  filename);
    checkArgument(Files.isRegularFile(path) && Files.isReadable(path), "The input file '%s' is not a regular file or readable", filename);
    return path;
  }

  @SneakyThrows
  private static String readFile(Path p){
    return new String(readAllBytes(p));
  }

}
