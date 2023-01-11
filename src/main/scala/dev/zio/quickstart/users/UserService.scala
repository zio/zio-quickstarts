package dev.zio.quickstart.users

import zio._

case class UserService(
    userRepo: UserRepo
) {

  def getUsers = userRepo.getUsers

  def putUser(user: User) = userRepo.putUser(user)
}

object UserService {

  def getUsers: RIO[UserService, List[User]] = ZIO.serviceWithZIO[UserService](_.getUsers)

  def putUser(user: User): RIO[UserService, User] = ZIO.serviceWithZIO[UserService](x => x.putUser(user))

  val layer = ZLayer.fromFunction(UserService.apply(_))
}
