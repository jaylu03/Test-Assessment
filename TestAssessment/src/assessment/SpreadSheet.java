package assessment;

import java.util.HashMap;
import java.util.Map;

public class SpreadSheet {

    private Map<String, Object> cells;
    private Map<String, Integer> cachedValues;

    public SpreadSheet() {
    	
        cells = new HashMap<>();
        cachedValues = new HashMap<>();
    }

    public void setCellValue(String cellId, Object value) {
    	
        if (!isValidCellId(cellId)) {
            throw new IllegalArgumentException("Invalid cell id: " + cellId);
        }

        if (!isValidCellValue(value)) {
            throw new IllegalArgumentException("Invalid cell value: " + value);
        }

        cells.put(cellId, value);
        cachedValues.remove(cellId);
    }

    public int getCellValue(String cellId) {
    	
        if (!isValidCellId(cellId)) {
            throw new IllegalArgumentException("Invalid cell id: " + cellId);
        }

        Object value = cells.get(cellId);

        if (value instanceof Integer) {
            return (int) value;
        }

        if (cachedValues.containsKey(cellId)) {
            return cachedValues.get(cellId);
        }

        if (value instanceof String) {
            String formula = (String) value;

            if (formula.startsWith("=")) {
                int calculatedValue = evaluateFormula(formula.substring(1));
                cachedValues.put(cellId, calculatedValue);
                return calculatedValue;
            }
        }

        throw new IllegalArgumentException("Invalid cell value: " + value);
    }

    private boolean isValidCellId(String cellId) {
        return cellId.matches("[A-Z]\\d+");
    }

    private boolean isValidCellValue(Object value) {
        return value instanceof Integer || (value instanceof String && ((String) value).startsWith("="));
    }

    private int evaluateFormula(String formula) {
    	
        if (cachedValues.containsKey(formula)) {
            return cachedValues.get(formula);
        }

        String[] tokens = formula.split("\\+");
        int result = 0;

        for (String token : tokens) {
        	
            if (token.matches("[A-Z]\\d+")) {
                int cellValue = getCellValue(token);
                result += cellValue;
            } else if (token.matches("\\d+")) {
                int value = Integer.parseInt(token);
                result += value;
            } else {
                throw new IllegalArgumentException("Invalid formula: " + formula);
            }
            
        }

        cachedValues.put(formula, result);
        return result;
    }

    public static void main(String[] args) {
    	
        SpreadSheet spreadsheet = new SpreadSheet();
        spreadsheet.setCellValue("A1", 13);
        spreadsheet.setCellValue("A2", 14);
        System.out.println(spreadsheet.getCellValue("A1")); // should print 13
        
        spreadsheet.setCellValue("A3", "=A1+A2");
        System.out.println(spreadsheet.getCellValue("A3")); // should print 27
        
        spreadsheet.setCellValue("A4", "=A1+A2+A3");
        System.out.println(spreadsheet.getCellValue("A4")); // should print 54

    }
}
