package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Реализация симуляции боя между армиями игрока и компьютера.
 *
 * Правила симуляции:
 * 1. В начале каждого раунда юниты сортируются по убыванию атаки
 * 2. Юниты атакуют по очереди в порядке сортировки
 * 3. Погибшие юниты (isAlive = false) пропускают ход
 * 4. Бой завершается, когда у одной армии не остаётся живых юнитов
 *
 * Сложность: O(n² log n), где n — общее количество юнитов
 */
public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    public void setPrintBattleLog(PrintBattleLog printBattleLog) {
        this.printBattleLog = printBattleLog;
    }

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Симуляция продолжается, пока обе армии имеют живых юнитов
        while (hasAliveUnits(playerArmy) && hasAliveUnits(computerArmy)) {
            // Собираем всех живых юнитов для текущего раунда
            List<Unit> turnQueue = new ArrayList<>();
            
            for (Unit unit : playerArmy.getUnits()) {
                if (unit.isAlive()) {
                    turnQueue.add(unit);
                }
            }
            
            for (Unit unit : computerArmy.getUnits()) {
                if (unit.isAlive()) {
                    turnQueue.add(unit);
                }
            }
            
            // Сортируем по убыванию значения атаки
            turnQueue.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());
            
            // Выполняем ходы всех юнитов в порядке очереди
            for (Unit unit : turnQueue) {
                // Пропускаем мёртвых юнитов (могли погибнуть в этом раунде)
                if (!unit.isAlive()) {
                    continue;
                }
                
                // Проверяем, что у обеих армий ещё есть живые юниты
                if (!hasAliveUnits(playerArmy) || !hasAliveUnits(computerArmy)) {
                    break;
                }
                
                // Выполняем атаку через программу юнита
                Unit target = unit.getProgram().attack();
                
                // Логируем атаку
                if (printBattleLog != null) {
                    printBattleLog.printBattleLog(unit, target);
                }
            }
        }
    }

    /**
     * Проверяет, есть ли в армии живые юниты.
     */
    private boolean hasAliveUnits(Army army) {
        if (army == null || army.getUnits() == null) {
            return false;
        }
        
        for (Unit unit : army.getUnits()) {
            if (unit.isAlive()) {
                return true;
            }
        }
        return false;
    }
}