package org.icgc.dcc.song.tools.exporter.filters;

import lombok.RequiredArgsConstructor;
import org.icgc.dcc.song.tools.exporter.model.PortalFileMetadata;

/**
 * Filters out files that map to blacklisted specimen Ids.
 */
@RequiredArgsConstructor
public class FileFilter implements Filter<PortalFileMetadata> {

  private final Filter<String> specimenIdFilter;

  @Override public boolean isPass(PortalFileMetadata portalFileMetadata) {
    return specimenIdFilter.passStream(portalFileMetadata.getSpecimenIds()).count() > 0;
  }

  public static FileFilter createFileFilter(Filter<String> specimenIdFilter) {
    return new FileFilter(specimenIdFilter);
  }

}
