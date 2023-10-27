package com.mydemo.demoproject.controller.shop;

import com.mydemo.demoproject.Entity.Address;
import com.mydemo.demoproject.Entity.CategoryInfo;
import com.mydemo.demoproject.Entity.UserEntity;
import com.mydemo.demoproject.service.shop.AddressService;
import com.mydemo.demoproject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/address")
public class AddressController {
    @Autowired
    AddressService addressService;
    @Autowired
    UserService userService;


  /*new update*/

    //    Address add
    @GetMapping("/addAddress")
    public String getAddressPage(@RequestParam(name = "uuid", required = false) UUID productUuid,
                                 @RequestParam(name = "quantity", required = false) Float quantity,Model model) {
        if(productUuid!=null&&quantity!=null)
        {
            model.addAttribute("productUuid",productUuid);
            model.addAttribute("quantity", quantity);
            return "shop/add-address";
        }else{

            return "shop/add-address";}
    }



    @PostMapping("/create")
    public String  createAddress(@RequestParam(name = "productUuid", required = false) UUID productUuid,
                                 @RequestParam(name = "quantity", required = false) Float quantity,
                                 @ModelAttribute Address address,
                                 @AuthenticationPrincipal(expression = "username")String  username){
        if (productUuid!=null&&quantity!=null) {
            address.setUserEntity(userService.getUserdata(username).orElse(null));

            addressService.saveToAddress(address);

            return "redirect:/order/show-single-product/" + productUuid + "/" + quantity;

        }
        address.setUserEntity(userService.getUserdata(username).orElse(null));
        addressService.saveToAddress(address);
        return "redirect:/address/show";
}



    /*Show address*/
    @GetMapping("/show")
    public String showAddress( @AuthenticationPrincipal(expression = "username")String  username,
                               Model model ) {
        List<Address> useraddress=addressService.findByUserEntity_Usernames(username);
        List<Address> enabledAddresses = useraddress.stream()
                .filter(Address::isEnabled)
                .collect(Collectors.toList());

        if(useraddress.isEmpty()) {
            model.addAttribute("userAddress empty", true);
        }
        else {

            model.addAttribute("useraddress", enabledAddresses);
        }
        return "shop/address";
}



  @GetMapping("/update/{uuid}")
    public String updateAddress(@PathVariable UUID uuid, Model model)
  {
      Optional<Address> addressList=addressService.findAddressById(uuid);
      model.addAttribute("addressList",addressList.orElse(null));
      return "shop/edit-address";
  }


    /*Update address post*/
    @PostMapping("/edit")
    public String  updatesAddress(@RequestParam("uuid") UUID uuid,
                                  @RequestParam("houseNumberOrName") String houseNumberOrName,
                                  @RequestParam("area") String area,
                                  @RequestParam("city") String city,
                                  @RequestParam("district") String district,
                                  @RequestParam("state") String state,
                                  @RequestParam("pincode") Long pincode,
                                  @RequestParam("landmark")String landmark){


       Optional<Address> address= addressService.findAddressById(uuid);
        if(address.isPresent()){
            Address addressList =address.get();

            addressList.setUuid(uuid);
            addressList.setHouseNumberOrName(houseNumberOrName);
            addressList.setCity(city);
            addressList.setArea(area);
            addressList.setPincode(pincode);
            addressList.setDistrict(district);
            addressList.setLandmark(landmark);
            addressList.setState(state);
            addressList.setEnabled(true);
            addressService.saveToAddress(addressList);
        }
        return  "redirect:/address/show";
    }

    /*block unblock*/

    /*Block Product*/
    @GetMapping("/disable/{uuid}")
    public String blockAddress(@PathVariable UUID uuid)
    {

        addressService.blockAddress(uuid);
        return "redirect:/address/show";
    }

    /*Un-block Product*/
    @GetMapping("/enable/{uuid}")
    public String unBlockAddress(@PathVariable UUID uuid)
    {

        addressService.unBlockAddress(uuid);
        return  "redirect:/address/show";
    }

  @GetMapping("/remove/{uuid}")
    public String removeAddress(@PathVariable UUID uuid){
      addressService.removeAddress(uuid);
      return "redirect:/address/show";
  }


    }
