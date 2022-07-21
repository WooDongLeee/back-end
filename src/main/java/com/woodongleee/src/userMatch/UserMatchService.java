package com.woodongleee.src.userMatch;

import com.woodongleee.config.BaseException;
import com.woodongleee.config.BaseResponseStatus;
import com.woodongleee.src.userMatch.Domain.CheckApplyingPossibilityRes;
import com.woodongleee.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@Service
public class UserMatchService {
    private final UserMatchDao userMatchDao;
    private final JwtService jwtService;

    @Autowired
    public UserMatchService(UserMatchDao userMatchDao, JwtService jwtService){
        this.userMatchDao = userMatchDao;
        this.jwtService = jwtService;
    }
    public void applyUserMatch(int userIdx, int matchPostIdx) throws BaseException {
        try {
            if(userMatchDao.checkMatchPostExist(matchPostIdx) != 1){
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR); // 존재하지 않는 matchPostIdx
            }

            CheckApplyingPossibilityRes checkApplyingPossibilityRes =  userMatchDao.checkApplyingPossibility(matchPostIdx);
            int headCnt = checkApplyingPossibilityRes.getHeadCnt();
            int joinCnt = checkApplyingPossibilityRes.getJoinCnt();
            int userMatchCnt = checkApplyingPossibilityRes.getUserMatchCnt();

            if((joinCnt + userMatchCnt) >= headCnt){
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR); // 용병 모집이 완료된 경기입니다.
            }


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime = checkApplyingPossibilityRes.getStartTime();
            String curTime = format.format(new Date());
            Date _startTime = format.parse(startTime);
            Date _curTime = format.parse(curTime);
            long diff = _startTime.getTime() - _curTime.getTime();

            if(diff < 0){
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR); // 용병 모집 기한이 지난 경기입니다.
            }


            userMatchDao.applyUserMatch(userIdx, matchPostIdx);
        }
        catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
