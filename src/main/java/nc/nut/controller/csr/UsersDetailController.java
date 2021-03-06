package nc.nut.controller.csr;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nc.nut.dao.product.ProductDao;
import nc.nut.dao.user.User;
import nc.nut.dao.user.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 28.04.2017.
 */
@Controller
@RequestMapping({"csr"})
public class UsersDetailController {
    @Resource
    private UserDAO userDAO;
    @Resource
    private ProductDao productDao;

    private List<User> clients;

    @RequestMapping(value = "getUsersPage", method = RequestMethod.GET)
    public String getUsers(Model model) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        clients=userDAO.getAllClients();
        model.addAttribute("userList",mapper.writeValueAsString(clients));
        return "csr/users";
    }


    @RequestMapping(value = "save",method=RequestMethod.POST)
    public String editUser(HttpSession session){

        session.removeAttribute("userId");
        return "csr/index";
    }

    @RequestMapping(value = "addProduct",method=RequestMethod.POST)
    public String addProduct(HttpSession session,Model model)throws  IOException{
//        int userId=(int)session.getAttribute("userId");
//        List<Product> products=productDao.getUnConnectedProducts(userId);
//        ObjectMapper mapper = new ObjectMapper();
//        model.addAttribute("productList",mapper.writeValueAsString(products));
        return "csr/productsForUser";
    }
    @RequestMapping(value="viewOrders",method=RequestMethod.POST)
    public String viewOrders(Model model) throws IOException{
        return"";
    }
    @RequestMapping(value="sendPassword",method=RequestMethod.POST)
    public String sendPassword(HttpSession session){
        return "csr/index";
    }

    @RequestMapping(value="getDetails",method =RequestMethod.GET)
    public ModelAndView getDetails(@RequestParam(value="id") int id, HttpSession session) throws IOException{
        User user;
        ModelAndView model=new ModelAndView("csr/userPage");
        for(User user1:clients){
            if(user1.getId()==id){
                user=user1;
                model.addObject("name",user.getName());
                model.addObject("surname",user.getSurname());
                model.addObject("email",user.getEmail());
                model.addObject("phone",user.getPhone());
                String[] address=user.getAddress().split(", ");
                model.addObject("city",address[0]);
                model.addObject("street",address[1]);
                model.addObject("building",address[2]);
                session.setAttribute("userId",user.getId());
                break;

            }
        }
        return model;
    }

}
