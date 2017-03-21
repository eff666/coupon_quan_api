//package com.shuyun.data.coupon.service.impl;
//
//
//import com.shuyun.data.coupon.dao.IUserDao;
//import com.shuyun.data.coupon.pojo.User;
//import com.shuyun.data.coupon.service.IUserService;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.List;
//
//@Service("userService")
//public class UserServiceImpl implements IUserService {
//	@Resource
//	private IUserDao userDao;
//	@Override
//	public User getUserById(int userId) {
//		// TODO Auto-generated method stub
//		return this.userDao.selectByPrimaryKey(userId);
//	}
//
//	@Override
//	public List<User> getUserList(int offset, int limit) {
//		// TODO Auto-generated method stub
//		return this.userDao.getUserList(offset,  limit);
//	}
//
//
//}
