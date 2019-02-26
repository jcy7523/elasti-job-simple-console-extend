package io.elasticjob.lite.lifecycle.domain;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class User implements Serializable{
	private static final long serialVersionUID = 3788214893499698082L;

	private String username;
	
	private String privileges;
	
	public User(final String username, final String privileges){
		this.username = username;
		this.privileges = privileges;
	}
	
}
