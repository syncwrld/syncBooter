package me.syncwrld.booter.minecraft.inventory;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ViewConfiguration {
  private boolean shouldUpdate;
  private boolean closeable;
  private int updateTime;
}
