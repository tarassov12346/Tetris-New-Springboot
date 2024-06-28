package com.app.game.tetris.persistence;

import com.app.game.tetris.model.Player;
import com.app.game.tetris.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;



@Component
public class Dao {
    public String bestPlayer = "To be shown";
    public int bestScore;



  /*  @Autowired
    HibernateSessionFactoryUtil hibernateSessionFactoryUtil;*/

    //   @Autowired
 //   private PlayerRepository playerRepository;




    public void recordScore(Player player) {
   /*     Session session = hibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(player);
        tx1.commit();
        session.close();*/

//       playerRepository.save(player);
    }

    public void retrieveScores() {
    /*    Session session = hibernateSessionFactoryUtil.getSessionFactory().openSession();

        CriteriaQuery cq =session.getCriteriaBuilder().createQuery(Player.class);
        cq.from(Player.class);

        List<Player> playerList = session.createQuery(cq).getResultList();
        session.close();
        Collections.sort(playerList, Comparator.comparingInt(Player::getPlayerScore));
        bestPlayer = playerList.get(playerList.size() - 1).getPlayerName();
        bestScore = playerList.get(playerList.size() - 1).getPlayerScore();*/
    }
}
