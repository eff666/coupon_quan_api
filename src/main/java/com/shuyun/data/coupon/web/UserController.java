//package com.shuyun.data.coupon.web;
//
//
//import com.shuyun.data.coupon.pojo.User;
//import com.shuyun.data.coupon.service.IUserService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//import javax.validation.constraints.Max;
//import javax.validation.constraints.NotNull;
//import java.util.List;
//
//@Controller
//@RequestMapping("/user")
//public class UserController {
//	@Resource
//	private IUserService userService;
//
//	@RequestMapping("/showUser")
//	public String toIndex(@Valid @RequestParam("id")  @Max(1) int id, Errors idErrors, Model model){
//		if(idErrors.hasErrors()){
//			User user = new User();
//			user.setUserName(idErrors.toString());
//			model.addAttribute("user", user);
//			return "showUser";
//		}
//		System.out.println("hello");
//
//		User user = this.userService.getUserById(id);
//		model.addAttribute("user", user);
//		return "showUser";
//	}
//
//	@RequestMapping("/getUser")
//	public @ResponseBody User getJson(HttpServletRequest request){
//		System.out.println("hello");
//		int userId = Integer.parseInt(request.getParameter("id"));
//		User user = this.userService.getUserById(userId);
//
//		return user;
//	}
//
//	@RequestMapping(value = "/list", method = RequestMethod.GET)
//	@ResponseBody
//	public List<User> list(Integer offset, Integer limit) {
//
//		offset = offset == null ? 0 : offset;//默认便宜0
//		limit = limit == null ? 50 : limit;//默认展示50条
//		List<User> list = userService.getUserList(offset, limit);
//
//		return list;
//	}
//}
