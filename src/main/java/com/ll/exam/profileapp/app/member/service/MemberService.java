package com.ll.exam.profileapp.app.member.service;

import com.ll.exam.profileapp.app.Util;
import com.ll.exam.profileapp.app.member.entity.Member;
import com.ll.exam.profileapp.app.member.respository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    @Value("${custom.genFileDirPath}")
    private String genFileDirPath;
    private final MemberRepository memberRepository;
    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }
    private String getCurrentProfileImgDirName() {
        return "member/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");
    }

    public Member join(String username, String password, String email, MultipartFile profileImg) {
        String profileImgRelPath = saveProfileImg(profileImg);

        Member member = Member.builder()
                .username(username)
                .email(email)
                .password(password)
                .profileImg(profileImgRelPath)
                .build();

        memberRepository.save(member);
        return member;
    }
    private String saveProfileImg(MultipartFile profileImg) {
        if ( profileImg == null || profileImg.isEmpty() ) {
            return null;
        }
        String profileImgDirName = getCurrentProfileImgDirName();

        String ext = Util.file.getExt(profileImg.getOriginalFilename());

        String fileName = UUID.randomUUID() + "." + ext;
        String profileImgDirPath = genFileDirPath + "/" + profileImgDirName;
        String profileImgFilePath = profileImgDirPath + "/" + fileName;

        new File(profileImgDirPath).mkdirs(); // 폴더가 혹시나 없다면 만들어준다.

        try {
            profileImg.transferTo(new File(profileImgFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return profileImgDirName + "/" + fileName;
    }

    public Member getMemberById(Long loginedMemberId) {
        Member member = memberRepository.findById(loginedMemberId).orElse(null);
        return member;
    }

    //시큐리티 꼭 있어야함

    public Member join(String username, String password, String email) {
        Member member = Member.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        memberRepository.save(member);
        return member;
    }

    public long count() {
        return memberRepository.count();
    }

    public void removeProfileImg(Member member) {
        member.removeProfileImgOnStorage(); //파일삭제
        member.setProfileImg(null);

        memberRepository.save(member);
    }

    public void setProfileImgByUrl(Member member, String url) {
        String filePath = Util.file.downloadImg(url, genFileDirPath + "/" + getCurrentProfileImgDirName() + "/" + UUID.randomUUID());
        member.setProfileImg(getCurrentProfileImgDirName() + "/" + new File(filePath).getName());
        memberRepository.save(member);
    }

    public void modify(Member member, String email, MultipartFile profileImg) {
        removeProfileImg(member);
        String profileImgRelPath = saveProfileImg(profileImg);

        member.setEmail(email);
        member.setProfileImg(profileImgRelPath);
        memberRepository.save(member);
    }

}
