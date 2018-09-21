package com.roberttisma.tools.icgc.portal.exporter.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.roberttisma.tools.icgc.portal.exporter.model.PortalFileMetadata;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getAccess;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getDataType;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getDonorId;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getExperimentalStrategy;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getFileFormat;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getFileId;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getFileLastModified;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getFileMd5sum;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getFileName;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getFileSize;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getGenomeBuild;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getIndexFileFileFormat;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getIndexFileFileMd5sum;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getIndexFileFileName;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getIndexFileFileSize;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getIndexFileId;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getIndexFileObjectId;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getObjectId;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getProjectCode;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getRepoDataBundleId;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getRepoMetadataPath;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getSampleIds;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getSoftware;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getSpecimenIds;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getSpecimenTypes;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getSubmittedDonorId;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getSubmittedSampleIds;
import static com.roberttisma.tools.icgc.portal.exporter.parser.FilePortalJsonParser.getSubmittedSpecimenIds;
import static lombok.Lombok.sneakyThrow;
import static org.icgc.dcc.song.core.utils.JsonUtils.toPrettyJson;

@Slf4j
@NoArgsConstructor
public class PortalFileMetadataConverter {

  public static PortalFileMetadata convertToPortalFileMetadata(JsonNode o, String repoName){
    try {
      return PortalFileMetadata.builder()
          .access              (getAccess(o))
          .repoDataBundleId    (getRepoDataBundleId(o, repoName))
          .repoMetadataPath    (getRepoMetadataPath(o, repoName))
          .dataType            (getDataType(o))
          .donorId             (getDonorId(o))
          .experimentalStrategy(getExperimentalStrategy(o))
          .fileFormat          (getFileFormat(o))
          .fileId              (getFileId(o))
          .fileLastModified    (getFileLastModified(o))
          .fileMd5sum          (getFileMd5sum(o))
          .fileName            (getFileName(o))
          .fileSize            (getFileSize(o))
          .genomeBuild         (getGenomeBuild(o))
          .indexFileFileFormat (getIndexFileFileFormat(o).orElse(null))
          .indexFileFileMd5sum (getIndexFileFileMd5sum(o).orElse(null))
          .indexFileFileName   (getIndexFileFileName(o).orElse(null))
          .indexFileFileSize   (getIndexFileFileSize(o).orElse(null))
          .indexFileId         (getIndexFileId(o).orElse(null))
          .indexFileObjectId   (getIndexFileObjectId(o).orElse(null))
          .objectId            (getObjectId(o))
          .projectCode         (getProjectCode(o))
          .sampleIds           (getSampleIds(o))
          .specimenIds         (getSpecimenIds(o))
          .specimenTypes        (getSpecimenTypes(o))
          .submittedDonorId    (getSubmittedDonorId(o))
          .submittedSampleIds  (getSubmittedSampleIds(o))
          .submittedSpecimenIds(getSubmittedSpecimenIds(o))
          .software            (getSoftware(o))
          .build();
    } catch (Throwable t){
      log.error("OBJECT_DATA:\n{}", toPrettyJson(o));
      throw sneakyThrow(t);
    }
  }


}
