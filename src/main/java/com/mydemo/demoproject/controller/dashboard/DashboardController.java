package com.mydemo.demoproject.controller.dashboard;

import com.mydemo.demoproject.Entity.Order;
import com.mydemo.demoproject.Entity.OrderItems;
import com.mydemo.demoproject.Entity.enumlist.ReportFrequency;
import com.mydemo.demoproject.Repository.admin.OrderRepo;
import com.mydemo.demoproject.service.admin.order.OrderService;
import com.mydemo.demoproject.service.admin.salesReport.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class DashboardController {
    @Autowired
    SalesReportService salesReportService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepo orderRepo;


    @GetMapping("/dashboard-sale")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showDashboards() {


        return "admin/dashboard;";

    }

    @PostMapping("/dashboard-sale")
    public String generateReport(@RequestParam("selectTimePeriod") String selectTimePeriod,
                                 Model model) {
        ReportFrequency timePeriod = ReportFrequency.valueOf(selectTimePeriod);
        List<Order> orderList = salesReportService.getOrderByTimePeriod(timePeriod);


        model.addAttribute("orderList", orderList);
        return "admin/sales-report"; // Redirect back to the dashboard page.
    }

    /*pdf*/
    @GetMapping("/generate-sales-report")
    public ResponseEntity<byte[]> generateSalesReport(@RequestParam(value = "format", required = false, defaultValue = "pdf") String format
    )
            throws Exception {


        List<Order> ordersInRange = orderService.getAllOrders();

        byte[] reportBytes;
        String contentType;
        String filename;

        if ("pdf".equals(format)) {

            reportBytes = salesReportService.generateSalesReport(ordersInRange);
            contentType = MediaType.APPLICATION_PDF_VALUE;
            filename = "SalesReport.pdf";

        } else if ("excel".equals(format)) {
            System.out.println("Selected Format:5 else-if " + format);
            reportBytes = salesReportService.generateExcelReport(ordersInRange);
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            filename = "SalesReport.xlsx";

        } else {
            throw new IllegalArgumentException("Invalid report format");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
    }


    @GetMapping("/getGraphReport")
    public ResponseEntity<Map<String, Object>> showGraphReport() {
        Map<String, Object> salesReport = salesReportService.getTotalSalesReprotInGraph();
        System.out.println("Sales report graph::::::::::::::::0000000" + salesReport);
        return ResponseEntity.ok(salesReport);
    }


    @GetMapping("/salesReportForGraph")
    public ResponseEntity<Map<String, Object>> graphFilterInGraph(@RequestParam("mode") String mode) throws Exception {
        ReportFrequency timePeriod = ReportFrequency.valueOf(mode);

        Map<String, Object> salesReport = salesReportService.getSalesReportForGraph(timePeriod);


        return ResponseEntity.ok(salesReport);
    }


}
