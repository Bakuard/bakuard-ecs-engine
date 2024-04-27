package com.bakuard.ecsEngine.system;

import com.bakuard.collections.DynamicArray;
import com.bakuard.ecsEngine.Game;

import java.util.HashMap;

/**
 * Хранит все системы используемые в игре. Менеджер не умеет самостоятельно обновлять системы, т.е.
 * не имеет никакой функциональности игрового цикла. Вместо этого, он решает две другие задачи:<br/>
 * 1. Хранит и предоставляет доступ ко всем системам используемым в игре. <br/>
 * 2. Позволяет сгруппировать системы. Группа систем - это всего лишь обычный список, которому
 *    назначено имя. Пользователь может самостоятельно добавлять и удалять группы, добавлять и удалять
 *    системы из групп. Отношения между системами и группами - многие-ко-многим. Одна и та же система
 *    может быть добавлена в одну и туже группу несколько раз. Менеджер предоставляет удобный способ
 *    обновить за раз все системы принадлежащие одной и той же группе.
 */
public final class SystemManager {

    private record RegisteredSystem(String systemName, System system) {}

    private final DynamicArray<RegisteredSystem> registeredSystems;
    private final HashMap<String, DynamicArray<SystemMeta>> groups;
    private final Game game;

    public SystemManager(Game game) {
        this.registeredSystems = new DynamicArray<>();
        this.groups = new HashMap<>();
        this.game = game;
    }

    /**
     * Добавляет или заменяет систему под указанным именем. Если происходит замена системы, то система будет
     * заменена во всех группах, к которым она принадлежит.
     * @return ссылку на этот же объект.
     */
    public SystemManager registerSystem(String systemName, System system) {
        int systemIndex = registeredSystems.linearSearch(rs -> rs.systemName().equals(systemName));
        if(systemIndex == -1) {
            registeredSystems.addLast(new RegisteredSystem(systemName, system));
        } else {
            registeredSystems.replace(systemIndex, new RegisteredSystem(systemName, system));
            groups.forEach((groupName, group) -> group.replaceAll(
                    (systemMeta, index) -> systemMeta.systemName().equals(systemName) ?
                            systemMeta.setSystem(system) :
                            systemMeta
                    )
            );
        }
        return this;
    }

    /**
     * Добавляет систему в конец указанной группы. Одна и та же система может быть добавлена несколько
     * раз в одну и туже группу. Если группы с указанным именем не существует, она будет автоматически
     * создана.
     * @return ссылку на этот же объект.
     * @throws UnregisteredSystemException если система с указанным именем не была зарегистрирована
     *                                     ({@link #registerSystem(String, System)}).
     */
    public SystemManager appendToGroup(String groupName, String systemName) {
        System system = tryGetSystem(systemName);
        DynamicArray<SystemMeta> group = getOrCreateGroup(groupName);
        group.addLast(new SystemMeta(systemName, groupName, group.size(), group.size() + 1, system));
        updateIndexAndSizeForEachSystem(groupName);
        return this;
    }

    /**
     * Добавляет систему в указанную группу, в заданную позицию. Одна и та же система может быть
     * добавлена несколько раз в одну и туже группу. Если группы с указанным именем не существует,
     * она будет автоматически создана.
     * @return ссылку на этот же объект.
     * @throws UnregisteredSystemException если система с указанным именем не была зарегистрирована
     *                                     ({@link #registerSystem(String, System)}).
     */
    public SystemManager insertIntoGroup(String groupName, String systemName, int index) {
        System system = tryGetSystem(systemName);
        DynamicArray<SystemMeta> group = getOrCreateGroup(groupName);
        group.insert(index, new SystemMeta(systemName, groupName, index, group.size() + 1, system));
        updateIndexAndSizeForEachSystem(groupName);
        return this;
    }

    /**
     * Заменяет все системы в указанной группе.
     * @return ссылку на этот же объект.
     */
    public SystemManager replaceAllForGroup(String groupName, String... systemNames) {
        DynamicArray<SystemMeta> group = new DynamicArray<>();
        for(int i = 0; i < systemNames.length; ++i) {
            String systemName = systemNames[i];
            System system = tryGetSystem(systemName);
            SystemMeta systemMeta = new SystemMeta(systemName, groupName, i, systemNames.length, system);
            group.addLast(systemMeta);
        }
        groups.put(groupName, group);
        return this;
    }

    /**
     * Удаляет систему из указанной группы.
     * @return ссылку на этот же объект.
     * @throws UnknownGroupException если нет группы с указанным именем.
     */
    public SystemManager removeFromGroup(String groupName, String systemName) {
        DynamicArray<SystemMeta> group = tryGetGroup(groupName);
        int wasDeleted = group.removeIf((systemMeta, index) -> systemMeta.systemName().equals(systemName));
        if(wasDeleted > 0) updateIndexAndSizeForEachSystem(groupName);
        return this;
    }

    /**
     * Удаляет систему с указанным именем из всех групп и отменяет её регистрацию в менеджере систем.
     * @return ссылку на этот же объект.
     */
    public SystemManager removeSystem(String systemName) {
        groups.keySet().forEach(groupName -> removeFromGroup(groupName, systemName));
        registeredSystems.removeIf((rs, index) -> rs.systemName().equals(systemName));
        return this;
    }

    /**
     * Обновляет все системы в группе в порядке их добавления в группу.
     * @return ссылку на этот же объект.
     * @throws UnknownGroupException если нет группы с указанным именем.
     */
    public SystemManager updateGroup(String groupName) {
        DynamicArray<SystemMeta> group = tryGetGroup(groupName);
        group.cloneAndMap((systemMeta, index) -> new SystemMeta(systemMeta))
                .forEach(systemMeta -> systemMeta.system().update(systemMeta, game));
        return this;
    }


    private System tryGetSystem(String systemName) {
        RegisteredSystem regSystem = registeredSystems.linearSearchObj(rs -> rs.systemName().equals(systemName));
        if(regSystem == null) {
            throw new UnregisteredSystemException("System with name='" + systemName + "' is not registered.");
        }
        return regSystem.system();
    }

    private DynamicArray<SystemMeta> tryGetGroup(String groupName) {
        DynamicArray<SystemMeta> group = groups.get(groupName);
        if(group == null) {
            throw new UnknownGroupException("There is not group with name='" + groupName + '\'');
        }
        return group;
    }

    private DynamicArray<SystemMeta> getOrCreateGroup(String groupName) {
        return groups.computeIfAbsent(groupName, key -> new DynamicArray<>());
    }

    private void updateIndexAndSizeForEachSystem(String groupName) {
        DynamicArray<SystemMeta> group = groups.get(groupName);
        group.replaceAll((systemMeta, index) -> systemMeta.setGroupSize(group.size()).setIndex(index));
    }
}
