package com.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientDetailVO {
    @NotNull
    String osArch;
    @NotNull
    String osName;
    @NotNull
    String osVersion;
    @NotNull
    int osBit;
    @NotNull
    int cpuCore;
    @NotNull
    String cpuName;
    @NotNull
    double memory;
    @NotNull
    double disk;
    @NotNull
    String ip;
}
