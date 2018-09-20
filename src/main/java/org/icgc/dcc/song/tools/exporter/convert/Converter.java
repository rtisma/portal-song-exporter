package org.icgc.dcc.song.tools.exporter.convert;

public interface Converter<I, O> {

  O convert(I in);

}
