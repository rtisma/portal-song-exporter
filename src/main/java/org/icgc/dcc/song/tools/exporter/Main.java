package org.icgc.dcc.song.tools.exporter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc.dcc.song.tools.exporter.model.PortalFileMetadata;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.file.Files.readAllBytes;
import static org.icgc.dcc.song.tools.exporter.Config.PORTAL_API_URL;
import static org.icgc.dcc.song.tools.exporter.Config.PORTAL_REPO_NAME;
import static org.icgc.dcc.song.tools.exporter.convert.PortalUrlConverter.createPortalUrlConverter;
import static org.icgc.dcc.song.tools.exporter.download.DownloadIterator.createDownloadIterator;
import static org.icgc.dcc.song.tools.exporter.download.PortalDonorIdFetcher.createPortalDonorIdFetcher;
import static org.icgc.dcc.song.tools.exporter.download.fetcher.DataFetcher.createDataFetcher;
import static org.icgc.dcc.song.tools.exporter.download.fetcher.DonorFetcher.createDonorFetcher;
import static org.icgc.dcc.song.tools.exporter.download.urlgenerator.impl.FilePortalUrlGenerator.createFilePortalUrlGenerator;
import static org.icgc.dcc.song.tools.exporter.filters.BypassFilter.createBypassFilter;

@Slf4j
public class Main {

  public static void main(String[] args){
    val inputJQLFile = getFileFromArgument(args);
    val jql = readFile(inputJQLFile);
    val portalDonorIdFetcher = createPortalDonorIdFetcher(PORTAL_API_URL);
    val donorFetcher = createDonorFetcher(portalDonorIdFetcher);
    val portalUrlConverter = createPortalUrlConverter(PORTAL_REPO_NAME);
    val filePortalUrlGenerator = createFilePortalUrlGenerator(PORTAL_API_URL, PORTAL_REPO_NAME , jql);
    val downloadIterator = createDownloadIterator(portalUrlConverter, filePortalUrlGenerator, 100, 100, 1);
    val dataFetcher = createDataFetcher(donorFetcher, createBypassFilter(PortalFileMetadata.class), downloadIterator );
    val list = dataFetcher.fetchPortalFileMetadata();
    log.info("sdfdsf");

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
