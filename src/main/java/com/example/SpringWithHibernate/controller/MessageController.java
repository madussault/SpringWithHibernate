package com.example.SpringWithHibernate.controller;

import com.example.SpringWithHibernate.entity.Message;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import util.HibernateUtil;

import java.util.List;

@Controller
public class MessageController {

    Transaction transaction = null;

    @GetMapping("/msg")
    public String showMessage(Model model){
        //créer un nouvel object a remplir pour la forme
        model.addAttribute("newMessage", new Message());

        //Envoie la liste d'objets trouvés depuis la DB pour qu'elle soit affiché dans le template
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List <Message> messages = session.createQuery("from Message", Message.class).list();
            model.addAttribute("msgList", messages);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return "msgpage";
    }

    @PostMapping("/msg")
    public String postMessage(@ModelAttribute Message newMessage){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the message object
            session.save(newMessage);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return "redirect:msg";
    }
}