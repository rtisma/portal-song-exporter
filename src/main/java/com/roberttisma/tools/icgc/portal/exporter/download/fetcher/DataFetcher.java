package com.roberttisma.tools.icgc.portal.exporter.download.fetcher;

import com.roberttisma.tools.icgc.portal.exporter.filters.Filter;
import com.roberttisma.tools.icgc.portal.exporter.model.PortalDonorMetadata;
import com.roberttisma.tools.icgc.portal.exporter.model.PortalFileMetadata;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.roberttisma.tools.icgc.portal.exporter.download.DownloadIterator;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;

@Slf4j
@RequiredArgsConstructor
public class DataFetcher {

  @NonNull private final DonorFetcher donorFetcher;
  @NonNull private final Filter<PortalFileMetadata> portalFileMetadataFilter;
  @NonNull private final DownloadIterator<PortalFileMetadata> portalFileMetadataDownloadIterator;

  private Set<String> getGoodDonorIdsOnly(Set<PortalDonorMetadata> portalDonorMetadatas){
    return portalDonorMetadatas.stream()
        .map(PortalDonorMetadata::getDonorId)
        .collect(toSet());
  }

  private List<PortalFileMetadata> filterPortalFileMetadata(List<PortalFileMetadata> portalFileMetadataListCandidate,
      Set<String> goodDonorIds){
    return portalFileMetadataListCandidate.stream()
        .filter(x -> goodDonorIds.contains(x.getDonorId()))
        .filter(portalFileMetadataFilter::isPass)
        .collect(toList());
  }

  private Set<PortalDonorMetadata> fetchPortalDonorMetadata(List<PortalFileMetadata> portalFileMetadataListCandidate){
    return donorFetcher.fetchPortalDonorMetadataSet(portalFileMetadataListCandidate);
  }

  public List<PortalFileMetadata> fetchPortalFileMetadata(){
    return portalFileMetadataDownloadIterator.stream()
        .collect(toImmutableList());
  }

  public static DataFetcher createDataFetcher( DonorFetcher donorFetcher,
      Filter<PortalFileMetadata> portalFileMetadataFilter,
      DownloadIterator<PortalFileMetadata> portalFileMetadataDownloadIterator) {
    return new DataFetcher(donorFetcher, portalFileMetadataFilter, portalFileMetadataDownloadIterator);
  }
}
