package com.roberttisma.tools.icgc.portal.exporter.convert;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.icgc.dcc.common.core.util.stream.Streams;
import com.roberttisma.tools.icgc.portal.exporter.model.PortalFileMetadata;

import java.net.URL;
import java.util.List;

import static com.roberttisma.tools.icgc.portal.exporter.convert.PortalFileMetadataConverter.convertToPortalFileMetadata;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;
import static org.icgc.dcc.song.core.utils.JsonUtils.read;

@RequiredArgsConstructor
public class PortalUrlConverter implements Converter<URL, List<PortalFileMetadata>> {

  private static final String HITS = "hits";

  @NonNull private final String repoName;

  @Override
  public List<PortalFileMetadata> convert(URL url) {
    return Streams.stream(getHits(read(url)))
        .map(x -> convertToPortalFileMetadata(x, repoName))
        .collect(toImmutableList());
  }

  private synchronized static JsonNode getHits(JsonNode result) {
    return result.get(HITS);
  }

  public static PortalUrlConverter createPortalUrlConverter(String repoName) {
    return new PortalUrlConverter(repoName);
  }

}
