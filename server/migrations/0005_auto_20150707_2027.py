# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0004_users_skills'),
    ]

    operations = [
        migrations.CreateModel(
            name='Projects',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('project_name', models.CharField(max_length=30)),
                ('project_summary', models.CharField(max_length=500)),
                ('date_created', models.DateField()),
                ('image_path', models.CharField(default=None, max_length=50, null=True, blank=True)),
            ],
        ),
        migrations.RemoveField(
            model_name='users_skills',
            name='skills',
        ),
        migrations.RemoveField(
            model_name='users_skills',
            name='users',
        ),
        migrations.AddField(
            model_name='types',
            name='users',
            field=models.ManyToManyField(to='server.Users'),
        ),
        migrations.AddField(
            model_name='users',
            name='image_path',
            field=models.CharField(default=None, max_length=50, null=True, blank=True),
        ),
        migrations.DeleteModel(
            name='Users_skills',
        ),
        migrations.AddField(
            model_name='projects',
            name='id_owner',
            field=models.ForeignKey(to='server.Users'),
        ),
        migrations.AddField(
            model_name='projects',
            name='id_types',
            field=models.ForeignKey(to='server.Types'),
        ),
        migrations.AddField(
            model_name='skills',
            name='projects',
            field=models.ManyToManyField(to='server.Projects'),
        ),
    ]
