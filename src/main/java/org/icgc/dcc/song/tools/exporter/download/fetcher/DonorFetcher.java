package org.icgc.dcc.song.tools.exporter.download.fetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc.dcc.song.tools.exporter.download.PortalDonorIdFetcher;
import org.icgc.dcc.song.tools.exporter.model.PortalDonorMetadata;
import org.icgc.dcc.song.tools.exporter.model.PortalFileMetadata;

import java.util.List;
import java.util.Set;

import static lombok.Lombok.sneakyThrow;
import static org.icgc.dcc.song.core.utils.JsonUtils.toPrettyJson;
import static org.icgc.dcc.song.tools.exporter.parser.DonorPortalJsonParser.getDonorId;
import static org.icgc.dcc.song.tools.exporter.parser.DonorPortalJsonParser.getGender;
import static org.icgc.dcc.song.tools.exporter.parser.DonorPortalJsonParser.getProjectId;
import static org.icgc.dcc.song.tools.exporter.parser.DonorPortalJsonParser.getProjectName;
import static org.icgc.dcc.song.tools.exporter.parser.DonorPortalJsonParser.getSubmittedDonorId;
import static org.icgc.dcc.song.tools.exporter.parser.NormalSpecimenParser.createNormalSpecimenParser;

@Slf4j
@RequiredArgsConstructor
public class DonorFetcher {

  private final PortalDonorIdFetcher portalDonorIdFetcher;


  public Set<PortalDonorMetadata> fetchPortalDonorMetadataSet(List<PortalFileMetadata> portalFileMetadataList){
    val donorSet = ImmutableSet.<PortalDonorMetadata>builder();
    val donorIdSet = Sets.<String>newHashSet();
    int numErrorDonorIds = 0;
    for (val portalFileMetadata : portalFileMetadataList){
      val donorId = portalFileMetadata.getDonorId();
      val fileId = portalFileMetadata.getFileId();
      if (!donorIdSet.contains(donorId)){ //Want to minimize redundant network traffic (fetching)
        try {
          val portalDonorMetadata = fetchPortalDonorMetadata(donorId);
          donorSet.add(portalDonorMetadata);
        } catch(Throwable t){
          log.error("DONOR_FETCH_ERROR[{}]: donorId [{}] data is malformed in FileId [{}]. Error recorded",
              ++numErrorDonorIds, donorId , fileId);
        }
        donorIdSet.add(donorId);
      }

    }
    return donorSet.build();
  }

  public PortalDonorMetadata fetchPortalDonorMetadata(String donorId){
    val donorMetadata = portalDonorIdFetcher.getDonorMetadata(donorId);
    return convertToPortalDonorMetadata(donorMetadata);
  }

  public static DonorFetcher createDonorFetcher(PortalDonorIdFetcher portalDonorIdFetcher){
    return new DonorFetcher(portalDonorIdFetcher);
  }

  public static PortalDonorMetadata convertToPortalDonorMetadata(JsonNode donor){
    try{
      val parser = createNormalSpecimenParser(donor);
      return PortalDonorMetadata.builder()
          .donorId(getDonorId(donor))
          .projectId(getProjectId(donor))
          .projectName(getProjectName(donor))
          .submittedDonorId(getSubmittedDonorId(donor))
          .gender(getGender(donor))
          .normalAnalyzedId(parser.getNormalAnalyzedId())
          .normalSampleId(parser.getNormalSampleId())
          .normalSpecimenId(parser.getNormalSpecimenId())
          .normalSpecimenType(parser.getNormalSpecimenType())
          .normalSubmittedSpecimenId(parser.getNormalSubmittedSpecimenId())
          .build();
    } catch(Throwable t){
      log.info("Error: {}\nOBJECT_DATA_DUMP:\n{}", t.getMessage(), toPrettyJson(donor));
      throw sneakyThrow(t);
    }
  }


}
