package com.bakuard.ecsEngine.system;

public record SystemMeta(String systemName,
                         String groupName,
                         int index,
                         int groupSize,
                         System system) {

    public SystemMeta setSystem(System system) {
        return new SystemMeta(systemName, groupName, index, groupSize, system);
    }

    public SystemMeta setIndex(int index) {
        return new SystemMeta(systemName, groupName, index, groupSize, system);
    }

    public SystemMeta setGroupSize(int groupSize) {
        return new SystemMeta(systemName, groupName, index, groupSize, system);
    }
}
