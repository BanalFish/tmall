package tmall.dao;

import tmall.bean.Product;
import tmall.bean.Review;
import tmall.bean.User;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewDAO {
    private Connection c;

    {
        try {
            c = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotal(){
        int total = 0;
        try (Statement s = c.createStatement();) {

            String sql = "select count(*) from Review";

            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return total;
    }

    public int getTotal(int pid){
        int total=0;
        String sql="select count(*) from Review where pid = "+pid;
        try(Statement s=c.createStatement()){
            ResultSet rs=s.executeQuery(sql);
            while(rs.next()){
                total=total+1;
                //total=rs.getInt(1)
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    public void add(Review bean){
        String sql="insert into review values(null,?,?,?,?)";
        try(PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,bean.getContent());
            ps.setInt(2,bean.getUser().getId());
            ps.setInt(3,bean.getProduct().getId());
            ps.setTimestamp(4, DateUtil.d2t(bean.getCreateDate()));

            ps.execute();

            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                //这里还要设置id 是因为之前只在数据库中没有设置了id 实体类没有设置
                //为什么呢 是用类.id的时候防止出现空值吗
                bean.setId(rs.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(Review bean){
        String sql = "update Review set content= ?, uid=?, pid=? , createDate = ? where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, bean.getContent());
            ps.setInt(2, bean.getUser().getId());
            ps.setInt(3, bean.getProduct().getId());
            ps.setTimestamp(4, DateUtil.d2t( bean.getCreateDate()) );
            ps.setInt(5, bean.getId());
            ps.execute();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void delete(int id){
        try (Statement s = c.createStatement();) {

            String sql = "delete from Review where id = " + id;

            s.execute(sql);

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public Review get(int id){
        Review review=null;
        String sql="select * from review where id = "+id;

        try(Statement s=c.createStatement()) {
            ResultSet rs=s.executeQuery(sql);
            while(rs.next()){
                review.setId(rs.getInt("id"));
                review.setContent(rs.getString("content"));
                review.setProduct(new ProductDAO().get(rs.getInt("pid")));
                review.setUser(new UserDAO().get(rs.getInt("uid")));
                review.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return review;
    }

    //获取指定产品的评价
    public List<Review> list(int pid){
        return list(pid,0,Short.MAX_VALUE);
    }

    //获取指定产品一共有多少条评价
    public int getCount(int pid){
        String sql = "select count(*) from Review where pid = ? ";

        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setInt(1, pid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return 0;
    }

    //获取指定产品的评价
    public List<Review> list(int pid, int start, int count){
        List<Review> beans = new ArrayList<Review>();

        String sql = "select * from Review where pid = ? order by id desc limit ?,? ";

        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setInt(1, pid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Review bean = new Review();
                int id = rs.getInt(1);
                int uid = rs.getInt("uid");
                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));

                String content = rs.getString("content");

                Product product = new ProductDAO().get(pid);
                User user = new UserDAO().get(uid);

                bean.setContent(content);
                bean.setCreateDate(createDate);
                bean.setId(id);
                bean.setProduct(product);
                bean.setUser(user);
                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }

    public boolean isExist(String content, int pid){
        String sql = "select * from Review where content = ? and pid = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, content);
            ps.setInt(2, pid);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return false;
    }



}
