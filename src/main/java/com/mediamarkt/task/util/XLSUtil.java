package com.mediamarkt.task.util;

import com.mediamarkt.task.model.Category;
import com.mediamarkt.task.model.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XLSUtil {

    private static final Logger LOG = LoggerFactory.getLogger(XLSUtil.class);

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"Id", "Title", "Description", "Published"};
    static String SHEET = "Sheet1";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<Category> excelToCategory(MultipartFile csvFile) {
        try (InputStream is = csvFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<Category> categories = new ArrayList<Category>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Category category = new Category();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            category.setCategoryId((long) currentCell.getNumericCellValue());
                            break;

                        case 1:
                            category.setName(currentCell.getStringCellValue());
                            break;

                        case 2:
                            category.setParentId(Double.valueOf(currentCell.getNumericCellValue()).longValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }

                categories.add(category);
            }

            return categories;
        } catch (IOException e) {
            throw new BusinessException("fail to parse Excel file: " + e.getMessage());
        }
    }

    public static List<Product> excelToProduct(MultipartFile csvFile) {
        try (InputStream is = csvFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<Product> products = new ArrayList<Product>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Product product = new Product();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    LOG.info("parsing cell: {}", currentCell);
                    switch (cellIdx) {
                        case 0:
                            product.setName(currentCell.getStringCellValue());
                            break;

                        case 1:
                            if (CellType.NUMERIC == currentCell.getCellType()) {
                                product.setCategory(String.valueOf(currentCell.getNumericCellValue()));
                            }
                            if (CellType.STRING == currentCell.getCellType()) {
                                product.setCategory(currentCell.getStringCellValue());
                            }
                            break;

                        case 2:
                            product.setOnlineStatus(currentCell.getStringCellValue());
                            break;

                        case 3:
                            product.setLongDescription(currentCell.getStringCellValue());
                            break;

                        case 4:
                            product.setShortDescription(currentCell.getStringCellValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }

                products.add(product);
            }

            return products;
        } catch (IOException e) {
            throw new BusinessException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
