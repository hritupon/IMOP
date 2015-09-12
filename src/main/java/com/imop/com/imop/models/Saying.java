package com.imop.com.imop.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * Created by hritupon on 9/6/2015.

@NoArgsConstructor
@RequiredArgsConstructor

 */

@AllArgsConstructor
public class Saying {
    @Getter @Setter
    private long id;

    @Getter @Setter @Length(max = 3)
    private String content;
}
