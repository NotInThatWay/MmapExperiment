package com.zcy.mmapexperiment.dao;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TickQuery {
    String stkCode;
    int num;
}
