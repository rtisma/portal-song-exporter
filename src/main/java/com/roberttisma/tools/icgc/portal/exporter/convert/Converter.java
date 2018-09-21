package com.roberttisma.tools.icgc.portal.exporter.convert;

public interface Converter<I, O> {

  O convert(I in);

}
