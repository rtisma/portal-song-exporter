package org.icgc.dcc.song.tools.exporter.resolvers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.icgc.dcc.song.tools.exporter.model.PortalSpecimenMetadata;

import java.util.EnumMap;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public enum SpecimenClasses {

  ADJECENT_NORMAL("Adjecent Normal", ".*adjecent.*"), //adjecent must be before normal
  NORMAL("Normal", ".*normal.*"),
  TUMOUR("Tumour", ".*tumour.*");

  public static EnumMap<SpecimenClasses, Pattern> map;

  static {
    map = new EnumMap<SpecimenClasses, Pattern>(SpecimenClasses.class);
    for (val specimenClass : values()){
      val pattern = Pattern.compile(specimenClass.getRegex(), CASE_INSENSITIVE);
      map.put(specimenClass, pattern);
    }
  }

  @Getter private final String specimenClassName;
  @Getter private final String regex;

  public static SpecimenClasses resolve(PortalSpecimenMetadata portalSpecimenMetadata) {
    return resolve(portalSpecimenMetadata.getType());
  }

  public static SpecimenClasses resolve(String specimenType){
    for (val specimenClass : values()){
      val pattern = map.get(specimenClass);
      val matcher = pattern.matcher(specimenType);
      if (matcher.matches()){
        return specimenClass;
      }
    }
    throw new IllegalStateException(format(
        "The portalSpecimenMetadata.type [%s] does not match the case insensitive regex for any of the following: %s",
        specimenType,
        stream(values())
            .map(SpecimenClasses::getRegex)
            .collect(joining(","))));
  }

}
