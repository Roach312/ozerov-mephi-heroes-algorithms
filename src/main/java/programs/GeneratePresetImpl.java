package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

/**
 * Реализация генерации пресета армии компьютера.
 * Использует жадный алгоритм для максимизации эффективности армии.
 * 
 * Критерии эффективности (в порядке приоритета):
 * 1. Соотношение атаки к стоимости (attack/cost) — первичный
 * 2. Соотношение здоровья к стоимости (health/cost) — вторичный
 * 
 * Ограничения:
 * - Максимум 11 юнитов каждого типа
 * - Суммарная стоимость не превышает maxPoints
 * 
 * Сложность: O(n * m), где n = типы юнитов (4), m = макс. юнитов в армии
 */
public class GeneratePresetImpl implements GeneratePreset {
    
    private static final int MAX_UNITS_PER_TYPE = 11;
    private static final int ARMY_WIDTH = 3;   // Колонки армии компьютера (x: 0, 1, 2)
    private static final int FIELD_HEIGHT = 21; // Высота поля (y: 0-20)

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // Защита от некорректных входных данных
        if (unitList == null || unitList.isEmpty() || maxPoints <= 0) {
            Army emptyArmy = new Army();
            emptyArmy.setPoints(0);
            return emptyArmy;
        }
        
        // Сортируем типы юнитов по эффективности (убывание)
        // Первичный критерий: attack/cost, вторичный: health/cost
        List<Unit> sortedUnits = new ArrayList<>(unitList);
        sortedUnits.sort((a, b) -> {
            double attackEffA = (double) a.getBaseAttack() / a.getCost();
            double attackEffB = (double) b.getBaseAttack() / b.getCost();
            int cmp = Double.compare(attackEffB, attackEffA);
            if (cmp != 0) return cmp;
            
            double healthEffA = (double) a.getHealth() / a.getCost();
            double healthEffB = (double) b.getHealth() / b.getCost();
            return Double.compare(healthEffB, healthEffA);
        });
        
        // Результат
        List<Unit> armyUnits = new ArrayList<>();
        int currentPoints = 0;
        Map<String, Integer> unitCountByType = new HashMap<>();
        int unitIndex = 0;
        
        // Жадно добавляем юнитов: на каждой итерации выбираем самого эффективного
        // из тех, кого ещё можно добавить (не превышен лимит типа и хватает очков)
        boolean added = true;
        while (added && currentPoints < maxPoints) {
            added = false;
            
            for (Unit template : sortedUnits) {
                String unitType = template.getUnitType();
                int count = unitCountByType.getOrDefault(unitType, 0);
                
                // Проверяем ограничение на количество юнитов данного типа
                if (count >= MAX_UNITS_PER_TYPE) {
                    continue;
                }
                
                // Проверяем, хватает ли очков
                if (currentPoints + template.getCost() > maxPoints) {
                    continue;
                }
                
                // Вычисляем позицию на поле: заполняем по рядам (y), в каждом ряду 3 колонки (x)
                int xCoord = unitIndex % ARMY_WIDTH;
                int yCoord = unitIndex / ARMY_WIDTH;
                
                // Проверяем границы поля
                if (yCoord >= FIELD_HEIGHT) {
                    break;
                }
                
                // Создаём юнита с именем в формате "Type Number" (требование игры для рендеринга)
                String name = template.getUnitType() + " " + (count + 1);
                
                Unit newUnit = new Unit(
                    name,
                    template.getUnitType(),
                    template.getHealth(),
                    template.getBaseAttack(),
                    template.getCost(),
                    template.getAttackType(),
                    template.getAttackBonuses(),
                    template.getDefenceBonuses(),
                    xCoord,
                    yCoord
                );
                
                armyUnits.add(newUnit);
                currentPoints += template.getCost();
                unitCountByType.put(unitType, count + 1);
                unitIndex++;
                added = true;
                
                // После добавления одного юнита возвращаемся к началу списка (жадный выбор)
                break;
            }
        }
        
        // Создаём армию и добавляем юнитов
        Army army = new Army();
        for (Unit unit : armyUnits) {
            army.getUnits().add(unit);
        }
        army.setPoints(currentPoints);
        
        return army;
    }
}