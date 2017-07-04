package com.adm.dictionary.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 面试题的Bean
 * Created by Homiss on 2017/6/13.
 */
@Entity
public class QuestionBean {
    @Id(autoincrement = true)
    private Long id;
    private String question;
    private Integer questionId;
    private String answer;
    private Integer level;
    private Integer strange;
    private String groupName;

    @Generated(hash = 1535374152)
    public QuestionBean(Long id, String question, Integer questionId, String answer, Integer level, Integer strange, String groupName) {
        this.id = id;
        this.question = question;
        this.questionId = questionId;
        this.answer = answer;
        this.level = level;
        this.strange = strange;
        this.groupName = groupName;
    }

    @Generated(hash = 842286453)
    public QuestionBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getStrange() {
        return strange;
    }

    public void setStrange(Integer strange) {
        this.strange = strange;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "QuestionBean{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", questionId=" + questionId +
                ", answer='" + answer + '\'' +
                ", level=" + level +
                ", strange=" + strange +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}

