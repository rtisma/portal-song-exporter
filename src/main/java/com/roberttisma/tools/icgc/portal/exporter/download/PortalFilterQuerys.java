package com.roberttisma.tools.icgc.portal.exporter.download;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static com.roberttisma.tools.icgc.portal.exporter.download.urlgenerator.UrlGenerator.createIs;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.json.JsonNodeBuilders.object;


@NoArgsConstructor(access = PRIVATE)
public class PortalFilterQuerys {

   public static ObjectNode buildRepoFilter(@NonNull String repoName) {
     return object()
         .with("file",
             object()
                 .with("repoName", createIs(repoName))
                 .with("fileFormat", createIs("VCF", "BAM"))
                 .with("experimentalStrategy", createIs("WGS", "RNA-Seq"))
         )
         .end();
   }


}
