package com.roberttisma.tools.icgc.portal.exporter.resolvers;

import com.roberttisma.tools.icgc.portal.exporter.model.PortalFileMetadata;
import com.roberttisma.tools.icgc.portal.exporter.model.PortalSampleMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public enum SampleTypes {

  DNA("DNA", "^WXS|WGS$"),
  RNA("RNA", "^RNA-Seq|miRNA-Seq$");

  public static EnumMap<SampleTypes, Pattern> map;

  static {
    map = new EnumMap<SampleTypes, Pattern>(SampleTypes.class);
    for (val sampleTypes: values()){
      val pattern = Pattern.compile(sampleTypes.getRegex());
      map.put(sampleTypes, pattern);
    }
  }

  @Getter private final String sampleTypeName;
  @Getter private final String regex;

  public static SampleTypes resolve(PortalSampleMetadata portalSampleMetadata){
    return resolve(portalSampleMetadata.getLibraryStrategy().orElse("").trim());
  }

  public static SampleTypes resolve(PortalFileMetadata portalFileMetadata){
    return resolve(portalFileMetadata.getExperimentalStrategy());
  }

  public static SampleTypes resolve(String libraryStrategy){
    for (val sampleTypes : values()){
      val pattern = map.get(sampleTypes);
      val matcher = pattern.matcher(libraryStrategy);
      if (matcher.matches()){
        return sampleTypes;
      }
    }
    throw new IllegalStateException(format(
        "The portalSampleMetadata.libraryStrategy [%s] does not match regex for any of the "
            + "following: [%s]",
        libraryStrategy,
        Arrays.stream(values())
            .map(SampleTypes::getRegex)
            .collect(joining("] , ["))));
  }




}
