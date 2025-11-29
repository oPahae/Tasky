package com.example.demo.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    
}

/* 
@Repository kadeclare wahed composent bach naccediw lbdd
linterface repository katheriter mn jpaRepository fiha par deafaut bzf dyal les methodes li t9dr tsta3mlhom bach t3mltkhdem des operations 3la lbdd
user huwa classe u integer huwa type dyal id dyal user
findByEmail hadi method li katkhlli t9lb 3la user b email dyalou
existsByEmail hadi method li kat3ti  wach kayn user b hadak email wla la
*/
