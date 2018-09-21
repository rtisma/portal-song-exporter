package com.roberttisma.tools.icgc.portal.exporter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.file.Files.readAllBytes;
import static com.roberttisma.tools.icgc.portal.exporter.parser.PortalUrlParser.extractJQLQuery;

@Slf4j
public class Main {

  @SneakyThrows
  public static void main(String[] args){
    val jqlQuery = extractJQLQuery(args[0]);
    val factory = Factory.builder()
        .batchSize(Config.BATCH_SIZE)
        .numThreads(Config.NUM_THREADS)
        .outputDirpath(Config.OUTPUT_DIRPATH)
        .portalApiUrl(Config.PORTAL_API_URL)
        .portalFetchSize(Config.PORTAL_FETCH_SIZE)
        .portalRepoName(Config.PORTAL_REPO_NAME)
        .songServerUrl(Config.SONG_COLLAB_URL)
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
