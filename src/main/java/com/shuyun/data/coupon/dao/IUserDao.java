//package com.shuyun.data.coupon.dao;
//
//
//import com.shuyun.data.coupon.pojo.User;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//
//public interface IUserDao {
//    int deleteByPrimaryKey(Integer id);
//
//    int insert(User record);
//
//    int insertSelective(User record);
//
//    User selectByPrimaryKey(Integer id);
//
//    int updateByPrimaryKeySelective(User record);
//
//    int updateByPrimaryKey(User record);
//
//    List<User> getUserList(@Param("offset") int offset, @Param("limit") int limit);
//}