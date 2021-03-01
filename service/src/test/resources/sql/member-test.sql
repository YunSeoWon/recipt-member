insert into RECIPT_MEMBER(member_no, email, password, name, mobile_no, member_status, follower_count, introduction)
values (1, 'test1@test.com', 'password', 'test1', '010-1111-1111', 1, 0, ''),
       (2, 'test2@test.com', 'password', 'test2', '010-1111-1111', 1, 0, ''),
       (3, 'test3@test.com', 'password', 'test3', '010-1111-1111', 1, 0, ''),
       (4, 'test4@test.com', 'password', 'test4', '010-1111-1111', 1, 0, '');

insert into FOLLOWER_MAPPING(follower_mapping_no, member_no, follower_no)
values (1, 1, 2),
       (2, 2, 1),
       (3, 3, 1),
       (4, 4, 1);