package com.mydemo.demoproject.service.admin.salesReport;


import com.mydemo.demoproject.Entity.Order;
import com.mydemo.demoproject.Entity.OrderItems;
import com.mydemo.demoproject.Entity.enumlist.OrderStatus;
import com.mydemo.demoproject.Entity.enumlist.ReportFrequency;
import com.mydemo.demoproject.Repository.admin.OrderRepo;
import jdk.jshell.Snippet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesReportServiceImp implements  SalesReportService {
    @Autowired
    OrderRepo orderRepo;


    @Override
    public List<Order> getOrderByTimePeriod(ReportFrequency timePeriod) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (timePeriod) {
            case DAILY -> {
                startDate = endDate;
                break;
            }
            case WEEKLY -> {
                startDate = endDate.minusDays(6);
                break;
            }
            case MONTHLY -> {
                startDate = endDate.minusMonths(1);
                break;
            }
            default -> {
                throw new IllegalArgumentException("unsupported time period " + timePeriod);
            }
        }
        return orderRepo.findByOrderDateBetween(startDate, endDate);
    }
/*new update graph */

    public Map<String, Object> getSalesReportForGraph(ReportFrequency timePeriod) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (timePeriod) {
            case DAILY:
                startDate = endDate.minusDays(1);
                break;
            case WEEKLY:
                startDate = endDate.minusDays(6);
                break;
            case MONTHLY:
                startDate = endDate.minusMonths(1);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time period " + timePeriod);
        }

        List<Order> orders = orderRepo.findByOrderDateBetween(startDate, endDate);
        Map<String, Double> monthlySales = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (Order order : orders) {
            String monthYear = order.getOrderDate().format(monthFormatter);
            double totalPrice = order.getTotalPrice();
            monthlySales.put(monthYear, monthlySales.getOrDefault(monthYear, 0.0) + totalPrice);
        }

        List<String> months = new ArrayList<>(monthlySales.keySet());
        List<Double> amounts = new ArrayList<>(monthlySales.values());

        Double totalRevenue = orders.stream()
                .mapToDouble(Order::getTotalPrice)
                .sum();

        Integer totalCount = orders.size();

        Map<String, Object> result = new HashMap<>();
        result.put("totalOrderCount", totalCount);
        result.put("totalRevenue", totalRevenue);
        result.put("monthlyLabels", months);
        result.put("monthlyAmounts", amounts);
        return result;
    }


















    /*pdf*/

    public byte[] generateSalesReport(List<Order> orders) throws Exception {
        double grandTotal = 0.0;
        System.out.println("grandTotal>>>>>>>>>>>>>>>>>>>>>>>>");
        String sales = "<html><body>";
        sales += "<h2>All-STAR SALES REPORT</h2>";
        sales += "<h4>Order summary</h4>";
        sales += "<h5>Total orders</h5>" + orders.size();

        sales += "<table width='100%' border='1' cellpadding='2'>";
        sales += "<tr>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>User</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Product</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Quantity</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Payment Mode</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Price</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Status</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Date</th>";
        sales += "<th style='background-color: #f2f2f2; text-align: left;'>Total</th>";
        sales += "</tr>";

        for (Order order : orders) {
            List<OrderItems> orderItems = order.getOrderItems();


            for (OrderItems orderItems1 : orderItems) {

                sales += "<tr>";
                sales += "<td>"+ order.getUserEntity().getFirstname() + "</td>";
                sales += "<td>" + orderItems1.getProductInfo().getName()+ "</td>";
                sales += "<td>" + orderItems1.getQuantity() + "</td>";
                sales += "<td>" + order.getPaymentMode() + "</td>";
                sales += "<td>" + orderItems1.getPrice() + "</td>";
                sales += "<td>" + order.getOrderStatus() + "</td>";
                sales += "<td>" + order.getOrderDate() + "</td>";
                sales += "<td>" + order.getTotalPrice()+ "</td>";
                sales += "</tr>";
                grandTotal += orderItems1.getPrice();
            }
        }

        sales += "</table>";
        sales += "<h5>Total sales</h5>" + grandTotal;
        sales += "</body></html>";

        ITextRenderer renderer = new ITextRenderer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        renderer.setDocumentFromString(sales);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }

    @Override
    public   Map<String, Object> getTotalSalesReprotInGraph() {


            List<Order> totalSales = orderRepo.findAll().stream()
                    .filter(order -> order.getOrderStatus() != OrderStatus.CANCELED)
                    .collect(Collectors.toList());

            Map<String, Double> monthlySales = totalSales.stream()
                    .collect(Collectors.groupingBy(order -> order.getOrderDate()
                                    .format(DateTimeFormatter.ofPattern("MMM yyyy")),
                            Collectors.summingDouble(Order::getTotalPrice)));

            Double totalRevenue = totalSales.stream()
                    .mapToDouble(Order::getTotalPrice)
                    .sum();

            Integer totalCount = totalSales.size();

        Map<String, Object> result = new HashMap<>();
        result.put("totalOrderCount", totalCount);
        result.put("totalRevenue", totalRevenue);
        result.put("monthlySales", monthlySales);
        System.out.println("Result>>>>>>>>>>>>>>>>>"+result);
           return result;
        }


    public byte[] generateExcelReport(List<Order> ordersList) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("SalesReport");
        System.out.println("Selected Format: service>>>>>excel>>>>>>>>>>>>>>>>>>>>>>>>>>>3 " + ordersList);
        // Create headers
        String[] headers = {"User", "Product", "Quantity", "Price", "Date"};
        XSSFRow headerRow = sheet.createRow(0);
        for (int col = 0; col < headers.length; col++) {
            XSSFCell cell = headerRow.createCell(col);
            cell.setCellValue(headers[col]);
        }
        System.out.println("Selected Format: service>>>>>excel>>>>>>>>>>>>>>>>>>>>>>>>>>>4 " + ordersList);
        // Populate data
        int rowNum = 1;
        for (Order order : ordersList) {
            List<OrderItems> orderItems= order.getOrderItems();
            for (OrderItems orders:orderItems) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getUserEntity().getFirstname());
                row.createCell(1).setCellValue(orders.getProductInfo().getName());
                row.createCell(2).setCellValue(orders.getQuantity());
                row.createCell(3).setCellValue(order.getTotalPrice());
                row.createCell(4).setCellValue(order.getOrderDate().toString());
            }
        }
        System.out.println("Selected Format: service>>>>>excel>>>>>>>>>>>>>>>>>>>>>>>>>>>5 " + ordersList);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }


}