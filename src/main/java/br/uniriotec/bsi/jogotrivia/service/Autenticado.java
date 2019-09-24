package br.uniriotec.bsi.jogotrivia.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Autenticado {
	Privilegio[] value() default {};
}
