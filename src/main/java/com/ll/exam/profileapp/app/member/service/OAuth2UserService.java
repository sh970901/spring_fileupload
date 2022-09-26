package com.ll.exam.profileapp.app.member.service;

import com.ll.exam.profileapp.app.member.entity.Member;
import com.ll.exam.profileapp.app.member.exception.MemberNotFoundException;
import com.ll.exam.profileapp.app.member.respository.MemberRepository;
import com.ll.exam.profileapp.app.security.dto.MemberContext;
import com.ll.exam.profileapp.app.security.exception.OAuthTypeMatchNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String oauthId = oAuth2User.getName();

        Member member = null;
        String oauthType = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        if (!"KAKAO".equals(oauthType)) {
            throw new OAuthTypeMatchNotFoundException();
        }

        if (isNew(oauthType, oauthId)) {
            switch (oauthType) {
                case "KAKAO" -> {
                    Map attributesProperties = (Map) attributes.get("properties");
                    Map attributesKakaoAcount = (Map) attributes.get("kakao_account");
                    String nickname = (String) attributesProperties.get("nickname");
                    //email을 안받아올 경우를 대비하여 구성, username도 따로 구성
                    String email = "%s@kakao.com".formatted(oauthId);
                    String username = "KAKAO_%s".formatted(oauthId);

                    if ((boolean) attributesKakaoAcount.get("has_email")) {
                        email = (String) attributesKakaoAcount.get("email");
                    }

                    member = Member.builder()
                            .email(email)
                            .username(username)
                            .password("")
                            .build();

                    memberRepository.save(member);
                }
            }
        }
        //이미 존재한다면 member에 담아서 리턴
        else {
            member = memberRepository.findByUsername("%s_%s".formatted(oauthType, oauthId))
                    .orElseThrow(MemberNotFoundException::new);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("member"));
        return new MemberContext(member, authorities, attributes, userNameAttributeName);
    }

    //이미 존재하는지 검색 
    private boolean isNew(String oAuthType, String oAuthId) {
        return memberRepository.findByUsername("%s_%s".formatted(oAuthType, oAuthId)).isEmpty();
    }
}