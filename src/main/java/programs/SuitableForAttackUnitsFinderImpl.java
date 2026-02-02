package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация поиска юнитов, подходящих для атаки.
 * Юнит подходит для атаки, если он не заблокирован другим юнитом своей армии.
 *
 * Блокировка определяется по y-координате:
 * - При атаке на левую армию (isLeftArmyTarget=true): подходит юнит с минимальной y
 *   в каждом ряду (не закрыт слева).
 * - При атаке на правую армию (isLeftArmyTarget=false): подходит юнит с максимальной y
 *   в каждом ряду (не закрыт справа).
 *
 * Сложность: O(n * m), где m = 3 (кол-во рядов), фактически O(n)
 */
public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> result = new ArrayList<>();
        
        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            
            // Первый проход: находим крайнее значение y среди живых юнитов
            int edgeY = isLeftArmyTarget ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            
            for (Unit unit : row) {
                if (!unit.isAlive()) {
                    continue;
                }
                
                int unitY = unit.getyCoordinate();
                
                if (isLeftArmyTarget) {
                    // Ищем минимальную y (крайний левый, не закрыт слева)
                    if (unitY < edgeY) {
                        edgeY = unitY;
                    }
                } else {
                    // Ищем максимальную y (крайний правый, не закрыт справа)
                    if (unitY > edgeY) {
                        edgeY = unitY;
                    }
                }
            }
            
            // Второй проход: добавляем всех живых юнитов с крайней y-координатой
            // (на случай, если несколько юнитов стоят на одной линии)
            if (edgeY != Integer.MAX_VALUE && edgeY != Integer.MIN_VALUE) {
                for (Unit unit : row) {
                    if (unit.isAlive() && unit.getyCoordinate() == edgeY) {
                        result.add(unit);
                    }
                }
            }
        }
        
        return result;
    }
}
