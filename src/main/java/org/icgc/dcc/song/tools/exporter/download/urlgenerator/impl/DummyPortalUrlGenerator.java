package org.icgc.dcc.song.tools.exporter.download.urlgenerator.impl;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.icgc.dcc.song.tools.exporter.download.urlgenerator.UrlGenerator;

import java.net.URL;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.icgc.dcc.common.core.json.JsonNodeBuilders.object;
import static org.icgc.dcc.song.tools.exporter.download.urlgenerator.UrlGenerator.createIs;

@RequiredArgsConstructor
public class DummyPortalUrlGenerator implements UrlGenerator {

  private static final String REPOSITORY_FILES_ENDPOINT = "/api/v1/repository/files";
  private static final String INCLUDE_PARAM = "include=facets";
  private static final Joiner AMPERSAND_JOINER = Joiner.on("&");

  private final String serverUrl;

  @Override
  @SneakyThrows
  public URL getUrl(int size, int from) {
    return new URL(
        AMPERSAND_JOINER.join(
            serverUrl+ REPOSITORY_FILES_ENDPOINT +"?",
            getFiltersParam(),
            getFromParam(from),
            INCLUDE_PARAM,
            getSizeParam(size)));
  }



  private static String getSizeParam(int size){
    return "size="+size;
  }
  private  static String getFromParam(int from){
    return "from="+from;
  }

  public static DummyPortalUrlGenerator createDummyPortalUrlGenerator(String serverUrl){
    return new DummyPortalUrlGenerator(serverUrl);
  }

  private static String getFiltersParam(){
    return "filters="+encodeFilter();
  }
  @SneakyThrows
  private static String encodeFilter(){
    return encode(createFilter().toString(), UTF_8.name());
  }

  private static ObjectNode createFilter(){
    return object()
        .with("file",
            object()
                .with("repoName", createIs("PCAWG - Chicago (TCGA)"))
                .with("dataType", createIs("SSM"))
                .with("study", createIs("PCAWG"))
                .with("fileFormat", createIs("VCF"))
                .with("software", createIs("Sanger variant call pipeline"))
                .with("experimentalStrategy", createIs("WGS"))
        )
        .end();
  }



}
