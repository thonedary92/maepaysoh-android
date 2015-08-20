package org.maepaysoh.maepaysohsdk;

import android.content.Context;
import android.database.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.maepaysoh.maepaysohsdk.api.CandidateService;
import org.maepaysoh.maepaysohsdk.api.RetrofitHelper;
import org.maepaysoh.maepaysohsdk.db.CandidateDao;
import org.maepaysoh.maepaysohsdk.models.Candidate;
import org.maepaysoh.maepaysohsdk.models.CandidateDetailReturnObject;
import org.maepaysoh.maepaysohsdk.models.CandidateListReturnObject;
import org.maepaysoh.maepaysohsdk.utils.CandidateAPIProperties;
import org.maepaysoh.maepaysohsdk.utils.CandidateAPIPropertiesMap;
import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * Created by yemyatthu on 8/11/15.
 */
public class CandidateAPIHelper {
  private RestAdapter mCandidateRestAdapter;
  private CandidateService mCandidateService;
  private CandidateDao mCandidateDao;
  private Context mContext;

  public CandidateAPIHelper(String token, Context context) {
    mCandidateRestAdapter = RetrofitHelper.getResAdapter(token);
    mCandidateService = mCandidateRestAdapter.create(CandidateService.class);
    mContext = context;
  }

  /**
   *
   * @param callback
   */
  public void getCandidatesAsync(Callback<CandidateListReturnObject> callback) {
    getCandidatesAsync(new CandidateAPIPropertiesMap(),callback);
  }

  /**
   *
   * @param propertiesMap
   * @param callback
   */
  public void getCandidatesAsync(CandidateAPIPropertiesMap propertiesMap, Callback<CandidateListReturnObject> callback) {
    String gender = propertiesMap.get(CandidateAPIProperties.GENDER);
    String religion = propertiesMap.get(CandidateAPIProperties.RELIGION);
    boolean withParty = propertiesMap.get(CandidateAPIProperties.WITH_PARTY);
    boolean unicode = propertiesMap.get(CandidateAPIProperties.IS_UNICODE);
    int firstPage = propertiesMap.get(CandidateAPIProperties.FIRST_PAGE);
    int perPage = propertiesMap.get(CandidateAPIProperties.PER_PAGE);

    Map<CandidateService.PARAM_FIELD, String> optionParams = new HashMap<>();
    if (withParty) {
      optionParams.put(CandidateService.PARAM_FIELD._with, Constants.WITH_PARTY);
    }
    if (unicode) {
      optionParams.put(CandidateService.PARAM_FIELD.font, Constants.UNICODE);
    } else {
      optionParams.put(CandidateService.PARAM_FIELD.font, Constants.ZAWGYI);
    }
    optionParams.put(CandidateService.PARAM_FIELD.gender,gender);
    optionParams.put(CandidateService.PARAM_FIELD.religion,religion);
    optionParams.put(CandidateService.PARAM_FIELD.page, String.valueOf(firstPage));
    optionParams.put(CandidateService.PARAM_FIELD.per_page, String.valueOf(perPage));
    mCandidateService.listCandidatesAsync(optionParams, callback);
  }

  public List<Candidate> getCandidates(){
    return getCandidates(new CandidateAPIPropertiesMap());
  }
  /**
   *
   * @param propertiesMap
   *
   */
  public List<Candidate> getCandidates(CandidateAPIPropertiesMap propertiesMap) {
    String gender = propertiesMap.get(CandidateAPIProperties.GENDER);
    String religion = propertiesMap.get(CandidateAPIProperties.RELIGION);
    boolean withParty = propertiesMap.get(CandidateAPIProperties.WITH_PARTY);
    boolean unicode = propertiesMap.get(CandidateAPIProperties.IS_UNICODE);
    int firstPage = propertiesMap.get(CandidateAPIProperties.FIRST_PAGE);
    int perPage = propertiesMap.get(CandidateAPIProperties.PER_PAGE);
    boolean cache = propertiesMap.get(CandidateAPIProperties.CACHE);

    mCandidateDao = new CandidateDao(mContext);
    Map<CandidateService.PARAM_FIELD, String> optionParams = new HashMap<>();
    if (withParty) {
      optionParams.put(CandidateService.PARAM_FIELD._with, Constants.WITH_PARTY);
    }
    if (unicode) {
      optionParams.put(CandidateService.PARAM_FIELD.font, Constants.UNICODE);
    } else {
      optionParams.put(CandidateService.PARAM_FIELD.font, Constants.ZAWGYI);
    }
    optionParams.put(CandidateService.PARAM_FIELD.page, String.valueOf(firstPage));
    optionParams.put(CandidateService.PARAM_FIELD.per_page, String.valueOf(perPage));
    optionParams.put(CandidateService.PARAM_FIELD.gender,gender);
    optionParams.put(CandidateService.PARAM_FIELD.religion,religion);

    CandidateListReturnObject returnObject = mCandidateService.listCandidates(optionParams);
    if (cache) {
      for (Candidate data : returnObject.getData()) {
        try {
          mCandidateDao.createCandidate(data);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return returnObject.getData();
  }

  /**
   *
   * @param candidateId
   * @param callback
   */
  public void getCandidateByIdAsync(String candidateId,
      Callback<CandidateDetailReturnObject> callback) {
    boolean unicode = Utils.isUniCode(mContext);
    getCandidateByIdAsync(candidateId, true, unicode, callback);
  }

  /**
   *
   * @param candidateId
   * @param withParty
   * @param callback
   */
  public void getCandidateByIdAsync(String candidateId, boolean withParty,
      Callback<CandidateDetailReturnObject> callback) {
    boolean unicode = Utils.isUniCode(mContext);
    getCandidateByIdAsync(candidateId, withParty, unicode, callback);
  }

  /**
   *
   * @param candidateId
   * @param withParty
   * @param unicode
   * @param callback
   */
  public void getCandidateByIdAsync(String candidateId, Boolean withParty, boolean unicode,
      Callback<CandidateDetailReturnObject> callback) {
    Map<CandidateService.PARAM_FIELD, String> optionParams = new HashMap<>();
    if (withParty) {
      optionParams.put(CandidateService.PARAM_FIELD._with, Constants.WITH_PARTY);
    }
    if (unicode) {
      optionParams.put(CandidateService.PARAM_FIELD.font, Constants.UNICODE);
    } else {
      optionParams.put(CandidateService.PARAM_FIELD.font, Constants.ZAWGYI);
    }
    mCandidateService.getCandidateByIdAsync(candidateId, optionParams, callback);
  }

  /**
   *
   * @param candidateId
   */
  public Candidate getCandidateById(String candidateId, boolean cache) {
    boolean unicode = Utils.isUniCode(mContext);
    return getCandidateById(candidateId, true, unicode, cache);
  }

  /**
   *
   * @param candidateId
   * @param withParty
   */
  public Candidate getCandidateById(String candidateId, boolean withParty, boolean cache) {
    boolean unicode = Utils.isUniCode(mContext);
    return getCandidateById(candidateId, withParty, unicode, cache);
  }

  /**
   *
   * @param candidateId
   * @param withParty
   * @param unicode
   */
  public Candidate getCandidateById(String candidateId, Boolean withParty, boolean unicode,
      boolean cache) {
    Map<CandidateService.PARAM_FIELD, String> optionParams = new HashMap<>();
    if (withParty) {
      optionParams.put(CandidateService.PARAM_FIELD._with, Constants.WITH_PARTY);
    }
    if (unicode) {
      optionParams.put(CandidateService.PARAM_FIELD.font, Constants.UNICODE);
    } else {
      optionParams.put(CandidateService.PARAM_FIELD.font, Constants.ZAWGYI);
    }
    CandidateDetailReturnObject returnObject =
        mCandidateService.getCandidateById(candidateId, optionParams);
    Candidate candidate = returnObject.getCandidate();
    if (cache) {
      mCandidateDao.createCandidate(candidate);
    }
    return candidate;
  }

  public List<Candidate> getCandidatesFromCache() {
    mCandidateDao = new CandidateDao(mContext);
    return mCandidateDao.getAllCandidateData();
  }

  public Candidate getCandidateByIdFromCache(String candidateId) {
    mCandidateDao = new CandidateDao(mContext);
    return mCandidateDao.getCandidateById(candidateId);
  }

  public List<Candidate> searchCandidateFromCache(String keyword){
    mCandidateDao = new CandidateDao(mContext);
    try {
      return mCandidateDao.searchCandidatesFromDb(keyword);
    }catch (SQLException e){
      e.printStackTrace();
      return null;
    }
  }
}
