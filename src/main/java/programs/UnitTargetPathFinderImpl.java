package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

/**
 * Реализация поиска кратчайшего пути между юнитами на игровом поле.
 * Использует алгоритм BFS (поиск в ширину) с поддержкой 8 направлений движения.
 * Сложность: O(WIDTH * HEIGHT) = O(567)
 */
public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    
    // 8 направлений движения: горизонталь, вертикаль и диагонали
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        { 0, -1},          { 0, 1},
        { 1, -1}, { 1, 0}, { 1, 1}
    };
    
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Защита от null
        if (attackUnit == null || targetUnit == null) {
            return Collections.emptyList();
        }
        
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int endX = targetUnit.getxCoordinate();
        int endY = targetUnit.getyCoordinate();
        
        // Если начало и конец совпадают
        if (startX == endX && startY == endY) {
            return Collections.singletonList(new Edge(startX, startY));
        }
        
        // Построение множества препятствий (занятые клетки)
        Set<String> obstacles = new HashSet<>();
        if (existingUnitList != null) {
            for (Unit unit : existingUnitList) {
                // Исключаем атакующего и цель, а также мёртвых юнитов
                if (unit != attackUnit && unit != targetUnit && unit.isAlive()) {
                    obstacles.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
                }
            }
        }
        
        // BFS
        Queue<int[]> queue = new LinkedList<>();
        Map<String, int[]> parent = new HashMap<>();
        
        String startKey = startX + "," + startY;
        queue.add(new int[]{startX, startY});
        parent.put(startKey, null);
        
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int currX = curr[0];
            int currY = curr[1];
            
            // Проверяем достижение цели
            if (currX == endX && currY == endY) {
                return reconstructPath(parent, endX, endY);
            }
            
            // Обход всех 8 направлений
            for (int[] dir : DIRECTIONS) {
                int nextX = currX + dir[0];
                int nextY = currY + dir[1];
                String nextKey = nextX + "," + nextY;
                
                // Проверка границ поля
                if (nextX < 0 || nextX >= WIDTH || nextY < 0 || nextY >= HEIGHT) {
                    continue;
                }
                
                // Проверка на препятствие и посещённость
                if (obstacles.contains(nextKey) || parent.containsKey(nextKey)) {
                    continue;
                }
                
                parent.put(nextKey, curr);
                queue.add(new int[]{nextX, nextY});
            }
        }
        
        // Путь не найден
        return Collections.emptyList();
    }

    /**
     * Восстановление пути от конечной точки к начальной.
     */
    private List<Edge> reconstructPath(Map<String, int[]> parent, int endX, int endY) {
        List<Edge> path = new ArrayList<>();
        int[] curr = new int[]{endX, endY};
        
        while (curr != null) {
            path.add(new Edge(curr[0], curr[1]));
            String key = curr[0] + "," + curr[1];
            curr = parent.get(key);
        }
        
        // Разворачиваем путь, чтобы он шёл от начала к концу
        Collections.reverse(path);
        return path;
    }
}
