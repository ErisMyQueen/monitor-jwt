package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
public class BaseDetail {
    String osArch;
    String osName;
    String osVersion;
    int osBit;
    int cpuCore;
    String cpuName;
    double memory;
    double disk;
    String ip;

}
