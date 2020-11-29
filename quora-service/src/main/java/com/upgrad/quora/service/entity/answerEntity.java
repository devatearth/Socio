package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

/*
This model class maps to the answer table in DB
 */
@Entity
@Table(name = "answer")
public class answerEntity {
    //id column is primary key
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //uuid column is universal unique identity field
    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    //ans column will contain the answer
    @Column(name = "ans")
    @Size(max = 255)
    private String answer;

    //date column will contain the date
    @Column(name = "date")
    private Timestamp date;

    //user_id column will contain user-id who the answer belongs to.
    @Column(name = "user_id")
    private Integer userId;

    //quertion_id column will contain the question ID.
    @Column(name = "question_id")
    private Integer questionId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }
}
