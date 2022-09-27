package com.ll.exam.profileapp.app.security.dto;

import com.ll.exam.profileapp.app.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class MemberContext extends User implements OAuth2User {
    private final Long id;
    private final String profileImgUrl;

    private final String email;
    @Setter
    private LocalDateTime modifyDate;

    private Map<String, Object> attributes;
    private String userNameAttributeName;

    public MemberContext(Member member, List<GrantedAuthority> authorities) {
        super(member.getUsername(), member.getPassword(), authorities);
        this.id = member.getId();
        this.email = member.getEmail();
        this.modifyDate = member.getModifyDate();
        this.profileImgUrl = member.getProfileImgUrl();
    }

    
    //카카오 로그인 용
    public MemberContext(Member member, List<GrantedAuthority> authorities, Map<String, Object> attributes, String userNameAttributeName) {
        this(member, authorities);
        this.attributes = attributes;
        this.userNameAttributeName = userNameAttributeName;
    }
    //카카오 로그인을 하면 나에게 부여되는 번호를 반환하는데 이정보는 attribute에 있다.
    @Override
    public String getName() {
        return this.getAttribute(this.userNameAttributeName).toString();
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return super.getAuthorities().stream().collect(Collectors.toSet());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public String getProfileImgRedirectUrl() {
        return "/member/profile/img/" + getId() + "?cacheKey=" + getModifyDate().toString();
    }


}
