package org.icgc.dcc.song.tools.exporter;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc.dcc.song.tools.exporter.download.fetcher.DataFetcher;
import org.icgc.dcc.song.tools.exporter.model.PortalFileMetadata;

import static org.icgc.dcc.song.tools.exporter.Config.PORTAL_API_URL;
import static org.icgc.dcc.song.tools.exporter.Config.PORTAL_REPO_NAME;
import static org.icgc.dcc.song.tools.exporter.convert.PortalUrlConverter.createPortalUrlConverter;
import static org.icgc.dcc.song.tools.exporter.download.DownloadIterator.createDownloadIterator;
import static org.icgc.dcc.song.tools.exporter.download.PortalDonorIdFetcher.createPortalDonorIdFetcher;
import static org.icgc.dcc.song.tools.exporter.download.fetcher.DonorFetcher.createDonorFetcher;
import static org.icgc.dcc.song.tools.exporter.download.urlgenerator.impl.FilePortalUrlGenerator.createFilePortalUrlGenerator;
import static org.icgc.dcc.song.tools.exporter.filters.BypassFilter.createBypassFilter;

@Slf4j
public class Main {

  public static void main(String[] args){

    val portalDonorIdFetcher = createPortalDonorIdFetcher(PORTAL_API_URL);
    val donorFetcher = createDonorFetcher(portalDonorIdFetcher);

    val portalUrlConverter = createPortalUrlConverter(PORTAL_REPO_NAME);
    val filePortalUrlGenerator = createFilePortalUrlGenerator(PORTAL_API_URL, PORTAL_REPO_NAME );
    val downloadIterator = createDownloadIterator(portalUrlConverter, filePortalUrlGenerator, 100, 100, 1);
    val dataFetcher = DataFetcher.createDataFetcher(donorFetcher, createBypassFilter(PortalFileMetadata.class), downloadIterator );
    val list = dataFetcher.fetchPortalFileMetadata();
    log.info("sdfdsf");


  }

}
