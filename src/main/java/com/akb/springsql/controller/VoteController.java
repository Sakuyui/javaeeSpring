package com.akb.springsql.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VoteController {

    @RequestMapping("vote")
    public String toVote(){
        return "vote";
    }
}
