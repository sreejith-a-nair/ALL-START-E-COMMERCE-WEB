package com.mydemo.demoproject.service.admin.salesReport;

import com.mydemo.demoproject.Entity.Order;
import com.mydemo.demoproject.Entity.enumlist.ReportFrequency;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface SalesReportService {


     List<Order> getOrderByTimePeriod(ReportFrequency timePeriod);

     byte[] generateExcelReport(List<Order> ordersList) throws Exception;

//     byte[] generateSalesReport(Page<Order> ordersPage) throws Exception;

      byte[] generateSalesReport(List<Order> orders) throws Exception;

    Map<String, Object> getTotalSalesReprotInGraph();

//    Map<String, Object> getFilterSalesReportInGraph( );
     Map<String, Object> getSalesReportForGraph(ReportFrequency timePeriod);
}
