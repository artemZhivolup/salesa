package com.salesa.controller.rest;

import com.salesa.entity.Advert;
import com.salesa.entity.AdvertRest;
import com.salesa.entity.Category;
import com.salesa.entity.User;
import com.salesa.security.UserSecurity;
import com.salesa.service.AdvertService;
import com.salesa.service.CategoryService;
import com.salesa.util.CategoryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class RestAddAdvertController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryParser categoryParser;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private UserSecurity userSecurity;
    @RequestMapping(value = "/api/addAdvert", method = RequestMethod.GET)
    public String addAdvert(HttpSession session, HttpServletResponse response) throws IOException {
        User user = userSecurity.getUserBySessionId(session.getId());
        if(user == null){
             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You have to authorize to be able add new advert";
        }
        /*for (Category category : categoryService.getAll()){
            response.getWriter().println(categoryParser.toJSON(category));
        }*/
        return "Please, add next attributes to your advert: title, text, price, currency(USD, EUR, UAH), categoryId and status";
    }

    @RequestMapping(value = "/api/addAdvert", method = RequestMethod.POST)
    public String addAdvert(HttpServletRequest httpServletRequest, HttpSession session, HttpServletResponse response) throws IOException {
        User user = userSecurity.getUserBySessionId(session.getId());
        if(user == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You have to authorize to be able add new advert";
        }
        String title = httpServletRequest.getParameter("title");
        String text = httpServletRequest.getParameter("text");
        double price = Double.parseDouble(httpServletRequest.getParameter("price"));
        String currency = httpServletRequest.getParameter("currency");
        String status = httpServletRequest.getParameter("status");
        switch (status){
            case "Забронировано": status = "H";
                break;
            case "Продано": status = "S";
                break;
            default: status = "A";
        }
        /*Integer categoryId = Integer.parseInt(httpServletRequest.getParameter("categoryId"));*/
        String categoryId = httpServletRequest.getParameter("categoryId");
        if(title == null || text == null || price == 0 || categoryId == null || currency == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Please, check your data";
        }
        log.info("Creating advert " + title + " " + price + " " + currency);
        AdvertRest advert = new AdvertRest();
        advert.setTitle(title);
        advert.setText(text);
        advert.setCategory(categoryId);
        advert.setPrice(price);
        advert.setCurrency(currency);
        advert.setStatus(status);
        advert.setModificationDate(LocalDateTime.now());
        advert.setUser(String.valueOf(user.getId()));
        advertService.saveAdvert(advert);
        response.setStatus(HttpServletResponse.SC_OK);
        return "advert was added. Thank you!";
    }
}
