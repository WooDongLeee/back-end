package com.woodongleee.src.userMatch;

import com.woodongleee.src.userMatch.Domain.CheckApplyingPossibilityRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserMatchDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){ this.jdbcTemplate = new JdbcTemplate(dataSource); }


    public int checkMatchPostExist(int matchPostIdx){ // 용병 모집글이 있는지 확인
        String checkMatchPostExistQuery = "select exists(select matchPostIdx from MatchPost where matchPostIdx = ?);";

        return this.jdbcTemplate.queryForObject(checkMatchPostExistQuery, int.class, matchPostIdx);
    }


    public CheckApplyingPossibilityRes checkApplyingPossibility(int matchPostIdx){ // 경기 인원수 초과 여부 확인
        String checkApplyingPossibilityByHeadCntQuery = "select headCnt, joinCnt, userMatchCnt, date, startTime from MatchPost as MP\n" +
                "    join TeamSchedule TS on MP.teamScheduleIdx = TS.teamScheduleIdx\n" +
                "where matchPostIdx=?;";

        return this.jdbcTemplate.queryForObject(checkApplyingPossibilityByHeadCntQuery,
                (rs, rowNum) -> new CheckApplyingPossibilityRes(
                        rs.getInt("headCnt"),
                        rs.getInt("joinCnt"),
                        rs.getInt("userMatchCnt"),
                        rs.getString("startTime"))
        , matchPostIdx);
    }

    public void applyUserMatch(int userIdx, int matchPostIdx) {
        String applyUserMatchQuery = "insert into MatchApply (userIdx, matchPostIdx) values(?, ?);";
        Object[] applyUserMatchParams = new Object[]{userIdx, matchPostIdx};

        this.jdbcTemplate.update(applyUserMatchQuery, applyUserMatchParams);
    }
}
