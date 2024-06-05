package com.chaher.keycloaktesting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//we must convert the role to ROLE_role name to be accepted by hasRole methode of SpringBoot
@Component
public class jwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    //converter to convert the Json Web Token
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.attribute}")
    private  String attribute ;
    @Value("${jwt.auth.converter.clientId}")
    private  String clientID ;
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        Collection<GrantedAuthority> authorities =
                Stream.concat(jwtGrantedAuthoritiesConverter.convert(source).stream(),
                        extractRoleValue(source).stream()).collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities,getClaimName(source));
    }

    private String getClaimName(Jwt source) {
        String claimName = JwtClaimNames.SUB;
        if (attribute !=null){
            claimName=attribute;
        }
        return source.getClaim(claimName);
    }

    private Collection<?extends GrantedAuthority> extractRoleValue(Jwt source) {
        Map<String, Object> resource_access ;
        Map<String,Object> resource ;
        Collection<String> Roles;
        //if we dont have resource
        if (source.getClaim("resource_access") == null ){
            return Set.of();
        }
        resource_access = source.getClaim("resource_access");
        //if the resource is not equale to our app
        if (resource_access.get(clientID) == null) {return Set.of();}
        resource= (Map<String, Object>) resource_access.get(clientID);
        Roles= (Collection<String>) resource.get("roles");
        return  Roles.stream().map(Role->new SimpleGrantedAuthority("ROLE_"+Role)).collect(Collectors.toSet());
    }

}
