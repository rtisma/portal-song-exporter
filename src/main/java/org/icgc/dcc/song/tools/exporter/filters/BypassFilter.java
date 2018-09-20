package org.icgc.dcc.song.tools.exporter.filters;

import lombok.NonNull;

public class BypassFilter<T> implements Filter<T>{

  @Override public boolean isPass(T t) {
    return true;
  }

  public static <T> BypassFilter<T> createBypassFilter(@NonNull Class<T> tClass) {
    return new BypassFilter<T>();
  }

}
